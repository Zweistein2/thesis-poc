package de.potionlabs.ffmlibrary.structure

import com.squareup.kotlinpoet.FileSpec

/**
 * Diese Klasse repräsentiert die native Bibliothek.
 * Sie wird aus den verschiedenen mit [de.potionlabs.ffmlibrary.annotations.NativeClass], [de.potionlabs.ffmlibrary.annotations.NativeStruct] und [de.potionlabs.ffmlibrary.annotations.NativeTuple] annotierten Klassen generiert, welche die gleiche native Bibliothek repräsentieren.
 * Sie beinhaltet alle Informationen über die native Bibliothek, wie z.B. die Plattform, für welche die Bibliothek generiert wird, und die Klassen, [de.potionlabs.ffmlibrary.utils.Struct]s und [de.potionlabs.ffmlibrary.utils.ReturnTuple]s, welche die Bibliothek enthält.
 *
 * @param[platform] Die Plattform, für welche die Bibliothek generiert wird.
 * @param[interfaces] Eine Liste der Klassen, welche die Bibliothek enthält.
 * @param[structs] Eine Liste der [de.potionlabs.ffmlibrary.utils.Struct]s, welche die Bibliothek enthält.
 * @param[tuples] Eine Liste der [de.potionlabs.ffmlibrary.utils.ReturnTuple]s, welche die Bibliothek enthält.
 */
data class FFMLibrary(
    var platform: String,
    val interfaces: MutableList<FFMInterface> = mutableListOf(),
    val structs: MutableList<FFMStruct> = mutableListOf(),
    val tuples: MutableList<FFMTuple> = mutableListOf(),
): FFMObject {
    /**
     * Methode um aus den Klassen und [de.potionlabs.ffmlibrary.utils.Struct]s die jeweiligen [FileSpec]s zu generieren.
     * Ruft für jedes dieser Objekte deren "toFileSpec"-Methode auf.
     * Generiert zusätzlich die "StructFactory".
     *
     * @return[List] Eine Liste der generierten [FileSpec]s, welche die native Bibliothek repräsentieren.
     */
    fun toFiles(): List<FileSpec> {
        val files = mutableListOf<FileSpec>()

        val linkerInterface = if(interfaces.any { it.containsLinker }) {
            interfaces.first { it.containsLinker }
        } else {
            null
        }

        tuples.forEach { tuple ->
            interfaces.firstOrNull { it.className == tuple.className }?.tuples?.add(tuple)
        }

        files.add(FFMStructFactory(structs.filter { it.callbackId != -1 }.toList(), structs.first().packageName.substringBeforeLast(".") + ".utils").toFileSpec())
        files.addAll(interfaces.map { it.toFileSpec(linkerInterface) })
        files.addAll(structs.map { it.toFileSpec() })

        return files
    }
}
