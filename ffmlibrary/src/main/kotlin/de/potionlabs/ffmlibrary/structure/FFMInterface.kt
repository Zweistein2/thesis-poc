package de.potionlabs.ffmlibrary.structure

import com.google.devtools.ksp.symbol.KSName
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import de.potionlabs.ffmlibrary.addImports
import de.potionlabs.ffmlibrary.utils.ReturnTuple
import java.lang.foreign.AddressLayout
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemorySegment
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.nio.file.Path

/**
 * Diese Klasse repräsentiert eine mit [de.potionlabs.ffmlibrary.annotations.NativeClass] annotierte Klasse der nativen Bibliothek.
 * Sie beinhaltet alle Informationen über die native Klasse, wie z.B. die Methoden, die sie enthält, und die Informationen über die native Bibliothek, zu welcher sie gehört.
 *
 * @param[libraryName] Der Name der nativen Bibliothek, zu welcher die Klasse gehört.
 * @param[fileName] Der Name der Datei, in welcher die Klasse generiert wird.
 * @param[className] Der Name der Klasse.
 * @param[packageName] Der Name des Packages, in welchem die Klasse generiert wird.
 * @param[path] Der Pfad zur nativen Bibliothek.
 * @param[externalLinker] Der Name des externen Linkers, falls dieser verwendet werden soll.
 * @param[containsLinker] Gibt an, ob die Klasse einen Linker enthält, welcher zum Aufrufen der nativen Methoden verwendet wird.
 * @param[isCClass] Gibt an, ob es sich bei der Klasse um eine C-Klasse handelt.
 * @param[methods] Eine Liste der Methoden, welche die Klasse enthält.
 * @param[tuples] Eine Liste der [ReturnTuple]s, welche von den Methoden der Klasse zurückgegeben werden können.
 */
data class FFMInterface(
    val libraryName: String,
    val fileName: String,
    val className: String,
    val packageName: String,
    val path: String,
    val externalLinker: String,
    val containsLinker: Boolean,
    val isCClass: Boolean,
    val methods: MutableList<FFMMethod> = mutableListOf(),
    val tuples: MutableList<FFMTuple> = mutableListOf(),
): FFMObject {
    private var linkerInterface: FFMInterface? = null

    /**
     * Methode um aus dem FFMInterface ein [FileSpec] zu generieren, welches die native Klasse darstellt.
     * Dabei werden alle Methoden und Tuples der Klasse berücksichtigt.
     *
     * @param[linkerInterface] Das FFMInterface, welches den Linker enthält, der zum Aufrufen der nativen Methoden verwendet wird.
     * Falls das aktuelle FFMInterface selbst einen Linker enthält, wird dieser verwendet.
     *
     * @return[FileSpec] Das generierte [FileSpec], welches die native Klasse darstellt.
     */
    fun toFileSpec(linkerInterface: FFMInterface?): FileSpec {
        this.linkerInterface = linkerInterface
        val file = FileSpec.builder(packageName, className)

        file.addImports()
        file.addType(toTypeSpec())

        return file.build()
    }

    /**
     * Methode um aus dem FFMInterface ein [TypeSpec] zu generieren.
     * Generiert Konstruktor, Properties und die Methoden der Klasse.
     *
     * @return[TypeSpec] Das generierte [TypeSpec], welches den Inhalt der nativen Klasse darstellt.
     */
    private fun toTypeSpec(): TypeSpec {
        val classType = TypeSpec.classBuilder(className)

        if(isCClass) {
            classType.primaryConstructor(FunSpec.constructorBuilder()
                .addParameter("address", MemorySegment::class)
                .build())
            .addProperty(PropertySpec.builder("address", MemorySegment::class, KModifier.PRIVATE)
                .initializer("address")
                .build())
        }
        classType.addSuperinterface(ClassName.bestGuess("$packageName.${fileName.substringBefore(".kt")}"))

        if(externalLinker.isBlank()) {
            val companionType = TypeSpec.companionObjectBuilder()

            companionType.addProperty(PropertySpec.builder("nativeLinker", Linker::class)
                .initializer("Linker.nativeLinker()")
                .build())
            companionType.addProperty(PropertySpec.builder("stdLibLookup", SymbolLookup::class)
                .initializer("nativeLinker.defaultLookup()")
                .build())

            val propertySpec = PropertySpec.builder("loaderLookup", SymbolLookup::class)

            if(path.isNotBlank()) {
                propertySpec.initializer("SymbolLookup.libraryLookup(Path.of(%1S), Arena.global())", path)
            } else {
                propertySpec.initializer("SymbolLookup.loaderLookup()")
            }

            classType.addType(companionType.addProperty(propertySpec.build()).build())
        }

        classType.addFunctions(methods.map { it.toFunSpec() })

        return classType.build()
    }

    /*
     * -------------------------- Extension Functions --------------------------
     */

    /**
     * Erweiterungs-Methode um dem [FileSpec.Builder] die benötigten Imports hinzuzufügen, basierend auf den Methoden und Tuples der Klasse.
     * Ebenso werden die notwendigen Imports für den Linker und die [de.potionlabs.ffmlibrary.utils.Struct]s hinzugefügt.
     */
    private fun FileSpec.Builder.addImports(): FileSpec.Builder = apply {
        val structImports = mutableListOf<KSName?>()
        methods.filter { it.hasPointerParams && it.parameters.any { param -> param.returnsStruct }}.flatMap { it.parameters }.filter { it.returnsStruct }.forEach {
            if(!structImports.contains(it.paramType.declaration.qualifiedName)) {
                structImports.add(it.paramType.declaration.qualifiedName)
            }
        }

        if(path.isNotBlank()) {
            addImports(Path::class)
        }

        if(externalLinker.isNotBlank()) {
            addImport("$externalLinker.Companion", "loaderLookup", "nativeLinker", "stdLibLookup")
        } else if(isCClass && linkerInterface != null && linkerInterface!!.packageName.isNotBlank() && linkerInterface!!.className.isNotBlank()) {
            addImport("${linkerInterface!!.packageName}.${linkerInterface!!.className}.Companion", "loaderLookup", "nativeLinker", "stdLibLookup")
        } else {
            addImports(Linker::class, SymbolLookup::class)
        }

        if(methods.any { it.hasPointerParams && it.parameters.any { param -> param.isSequence }}) {
            addImports(MemoryLayout::class)
        }

        if(methods.any { it.hasPointerParams }) {
            addImports(ReturnTuple::class)
            tuples.forEach { addImport(it.packageName, it.fileName) }
        }
        structImports.filterNotNull().forEach {
            if(it.getShortName() == "Struct") {
                addImport(this@FFMInterface.packageName.substringBeforeLast(".") + ".utils", "CallbackStructFactory")
            } else {
                addImport(it.getQualifier(), it.getShortName(), "${it.getShortName()}Struct")
            }
        }
        addImports(FunctionDescriptor::class, Arena::class, MemorySegment::class, ValueLayout::class, AddressLayout::class)
    }
}
