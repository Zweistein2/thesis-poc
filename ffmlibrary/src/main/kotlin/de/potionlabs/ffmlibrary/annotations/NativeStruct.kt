package de.potionlabs.ffmlibrary.annotations

import de.potionlabs.ffmlibrary.utils.Struct

/**
 * Annotation für die Markierung einer Klasse als Struktur der nativen Bibliothek. Diese Klasse muss das Interface [Struct] implementieren.
 *
 * @param[libraryName] Der Name der nativen Bibliothek, zu welcher die Struktur gehört.
 * @param[id] Eine optionale ID, die zur Identifikation der Struktur verwendet werden kann. Findet Verwendung für die Erstellung der CallbackStructFactory. Standardmäßig: -1.
 */
@Target(AnnotationTarget.CLASS)
annotation class NativeStruct(
    val libraryName: String,
    val id: Int = -1
)