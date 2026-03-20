package de.potionlabs.ffmlibrary.structure

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import de.potionlabs.ffmlibrary.utils.StructImpl
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemorySegment

/**
 * Diese Klasse repräsentiert eine Factory, welche für die Erstellung der Callback-[de.potionlabs.ffmlibrary.utils.Struct]s der nativen Bibliothek verantwortlich ist.
 * Sie bietet zusätzlich eine Hilfsmethode an, die es ermöglicht das Speicherlayout eines Callback-[de.potionlabs.ffmlibrary.utils.Struct]s abzufragen.
 * Diese wird im Binding-Code für native Funktionen genutzt, die unterschiedliche Callbacks zurückgeben.
 *
 * @param[callbackStructs] Eine Liste an [FFMStruct]s, welche die Callback-Strukturen repräsentieren, für welche die Factory zuständig ist.
 * @param[packageName] Der Name des Packages, in welchem die Factory generiert wird.
 */
class FFMStructFactory(
    val callbackStructs: List<FFMStruct>,
    val packageName: String
) : FFMObject {
    /**
     * Methode um aus der FFMStructFactory ein [FileSpec] zu generieren, welches die StructFactory darstellt.
     * Dabei werden alle [FFMStruct]s berücksichtigt.
     *
     * @return[FileSpec] Das generierte [FileSpec], welches die StructFactory darstellt.
     */
    fun toFileSpec(): FileSpec {
        val file = FileSpec.builder(packageName, "CallbackStructFactory")

        callbackStructs.forEach {
            file.addImport(it.packageName, it.className)
        }

        file.addType(TypeSpec.objectBuilder("CallbackStructFactory")
            .addFunction(createMemoryLayoutMapping())
            .addFunction(createStructMapping())
            .build())

        return file.build()
    }

    /**
     * Methode um den Code für die Abfrage des Speicherlayouts des jeweiligen Callback-[de.potionlabs.ffmlibrary.utils.Struct]s anhand der Callback-ID zu generieren.
     *
     * @return[FunSpec] Das generierte [FunSpec], welches die Speicherlayout-Abfrage-Methode darstellt.
     */
    private fun createMemoryLayoutMapping(): FunSpec {
        val whenBlock = CodeBlock.builder()
            .beginControlFlow("return when(id)")
        callbackStructs.forEach {
            whenBlock.addStatement("%L -> %L.LAYOUT", it.callbackId, it.className)
        }
        whenBlock.addStatement("else -> throw IllegalArgumentException(\"No struct found for id \$id\")")
            .endControlFlow()

        return FunSpec.builder("getMemoryLayoutForId")
            .addParameter("id", Int::class)
            .addCode(whenBlock.build())
            .returns(MemoryLayout::class.asTypeName())
            .build()
    }

    /**
     * Methode um den Code für die Erstellung des jeweiligen Callback-[de.potionlabs.ffmlibrary.utils.Struct]s anhand der Callback-ID zu generieren.
     *
     * @return[FunSpec] Das generierte [FunSpec], welches die Callback-Erzeugungs-Methode darstellt.
     */
    private fun createStructMapping(): FunSpec {
        val whenBlock = CodeBlock.builder()
            .beginControlFlow("return when(id)")
        callbackStructs.forEach {
            whenBlock.addStatement("%L -> %L(content)", it.callbackId, it.className)
        }
        whenBlock.addStatement("else -> throw IllegalArgumentException(\"No struct found for id \$id\")")
            .endControlFlow()

        return FunSpec.builder("getStructForId")
            .addParameter("id", Int::class)
            .addParameter("content", MemorySegment::class)
            .addCode(whenBlock.build())
            .returns(StructImpl::class.asTypeName())
            .build()
    }
}