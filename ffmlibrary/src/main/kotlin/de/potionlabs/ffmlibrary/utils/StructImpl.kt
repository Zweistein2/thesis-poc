package de.potionlabs.ffmlibrary.utils

/**
 * Interface für die Kennzeichnung einer Klasse als generierte Struktur.
 * Wird primär von der [de.potionlabs.ffmlibrary.structure.FFMStructFactory] genutzt.
 */
interface StructImpl {
    fun mapToKotlinStruct(): Struct
}