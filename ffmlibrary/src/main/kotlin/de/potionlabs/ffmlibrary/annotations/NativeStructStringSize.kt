package de.potionlabs.ffmlibrary.annotations

/**
 * Annotation für die Länge eines Strings innerhalb einer Struktur. Muss innerhalb einer [NativeStruct]-annotierten Klasse verwendet werden.
 *
 * @param[size] Gibt die maximale Länge des Strings an.
 */
@Target(AnnotationTarget.FIELD)
annotation class NativeStructStringSize(
    val size: Long
)