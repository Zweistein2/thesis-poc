package de.potionlabs.ffmlibrary.structure

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import de.potionlabs.ffmlibrary.FFMProcessor.Companion.getTypeAlignment
import de.potionlabs.ffmlibrary.FFMProcessor.Companion.getTypeMapping
import de.potionlabs.ffmlibrary.addImports
import de.potionlabs.ffmlibrary.utils.StructImpl
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import kotlin.math.max

/**
 * Diese Klasse repräsentiert eine mit [de.potionlabs.ffmlibrary.annotations.NativeStruct] annotierte Struktur der nativen Bibliothek.
 * Sie beinhaltet alle Informationen über die native Struktur, wie z.B. die Attribute, die sie enthält, und die Informationen über die native Bibliothek, zu welcher sie gehört.
 *
 * @param[libraryName] Der Name der nativen Bibliothek, zu welcher die Struktur gehört.
 * @param[fileName] Der Name der Datei, in welcher die Struktur generiert wird.
 * @param[className] Der Name der Klasse, welche die Struktur repräsentiert.
 * @param[packageName] Der Name des Packages, in welchem die Struktur generiert wird.
 * @param[fields] Eine Liste der Attribute, welche die Struktur enthält.
 * @param[structPadding] Die Anzahl der Padding-Bytes, welche am Ende der Struktur hinzugefügt werden müssen, um die richtige Ausrichtung der Struktur zu gewährleisten.
 * @param[callbackId] Die ID des Callbacks, welcher die Struktur repräsentiert. Diese wird benötigt, um die Struktur in den generierten Methoden der StructFactory korrekt zu verwenden, da die Strukturen über deren Callback-ID referenziert werden.
 */
data class FFMStruct(
    val libraryName: String,
    val fileName: String,
    val className: String,
    val packageName: String,
    val fields: MutableList<FFMStructField> = mutableListOf(),
    var structPadding: Long = 0L,
    val callbackId: Int
): FFMObject {
    /**
     * Methode um aus dem FFMStruct ein [FileSpec] zu generieren, welches die native Struktur darstellt.
     * Dabei werden alle Attribute der Klasse berücksichtigt.
     *
     * @return[FileSpec] Das generierte [FileSpec], welches die native Struktur darstellt.
     */
    fun toFileSpec(): FileSpec {
        val file = FileSpec.builder(packageName, "${fileName.substringBefore(".kt")}Struct")

        calculatePadding()
        file.addType(toTypeSpec())
        file.addImports(MemoryLayout::class, MemorySegment::class, ValueLayout::class)

        return file.build()
    }

    /**
     * Methode um das notwendige Padding für die Attribute und die Struktur zu berechnen.
     * Dies ist notwendig, um die Struktur korrekt aus dem Speicher auslesen zu können.
     */
    private fun calculatePadding() {
        var offset = 0L
        var maxAlignment = 1L

        for (field in fields) {
            var typeAlignment = getTypeAlignment(field.fieldType)

            // Chars in Java sind 2 Bytes groß, aber in C nur 1 Byte, daher arbeiten wir hier mit JAVA_BYTE statt JAVA_CHAR
            if(field.fieldType.toString() == "String") {
                typeAlignment = ValueLayout.JAVA_BYTE.byteAlignment()
            }
            maxAlignment = max(maxAlignment, typeAlignment)

            val padding = (typeAlignment - (offset % typeAlignment)) % typeAlignment

            offset += padding

            field.padding = padding

            offset += field.stringSize?.times(typeAlignment) ?: typeAlignment
        }

        structPadding = (maxAlignment - (offset % maxAlignment)) % maxAlignment
    }

    /**
     * Methode um aus dem FFMStruct ein [TypeSpec] zu generieren.
     * Generiert Konstruktor, Properties und das Speicher-Layout der Struktur.
     *
     * @return[TypeSpec] Das generierte [TypeSpec], welches den Inhalt der nativen Struktur darstellt.
     */
    private fun toTypeSpec(): TypeSpec {
        val classType = TypeSpec.classBuilder(className)

        classType.addSuperinterface(StructImpl::class)
            .primaryConstructor(FunSpec.constructorBuilder()
                .addParameter("segment", MemorySegment::class)
                .build())
            .addProperty(PropertySpec.builder("segment", MemorySegment::class)
                .initializer("segment")
                .build())

        val companionType = TypeSpec.companionObjectBuilder()
        val structString = fields.joinToString(",\n\t") {
            if(it.fieldType.toString() == "String") {
                if(it.padding > 0) {
                    "MemoryLayout.paddingLayout(${it.padding}),\n\tMemoryLayout.sequenceLayout(${it.stringSize}, ValueLayout.JAVA_BYTE).withName(\"${it.fieldName}\")"
                } else {
                    "MemoryLayout.sequenceLayout(${it.stringSize}, ValueLayout.JAVA_BYTE).withName(\"${it.fieldName}\")"
                }
            } else {
                if(it.padding > 0) {
                    "MemoryLayout.paddingLayout(${it.padding}),\n\tValueLayout.${getTypeMapping(it.fieldType)}.withName(\"${it.fieldName}\")"
                } else {
                    "ValueLayout.${getTypeMapping(it.fieldType)}.withName(\"${it.fieldName}\")"
                }
            }
        }
        if(structPadding > 0) {
            companionType.addProperty(PropertySpec.builder("LAYOUT", MemoryLayout::class)
                .initializer("MemoryLayout.structLayout(\n\t$structString,\n\tMemoryLayout.paddingLayout($structPadding)\n)")
                .build())
        } else {
            companionType.addProperty(PropertySpec.builder("LAYOUT", MemoryLayout::class)
                .initializer("MemoryLayout.structLayout(\n\t$structString\n)")
                .build())
        }
        classType.addType(companionType.build())

        val mapFunction = FunSpec.builder("mapToKotlinStruct")
        mapFunction.addModifiers(KModifier.OVERRIDE)
        mapFunction.returns(ClassName.bestGuess("$packageName.${fileName.substringBefore(".kt")}"))
        fields.forEach {
            if(it.fieldType.toString() == "String") {
                mapFunction.addStatement("val %1L = segment.asSlice(LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement(%1S)), ${it.stringSize}).getString(0)", it.fieldName)
            } else {
                mapFunction.addStatement("val %1L = segment.get(ValueLayout.%2L, LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement(%1S)))", it.fieldName, getTypeMapping(it.fieldType))
            }
        }
        mapFunction.addStatement("return ${fileName.substringBefore(".kt")}(%1L)", fields.joinToString(", ") { it.fieldName })

        classType.addFunction(mapFunction.build())

        return classType.build()
    }
}
