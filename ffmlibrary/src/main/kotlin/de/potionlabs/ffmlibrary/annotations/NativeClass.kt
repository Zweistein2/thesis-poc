package de.potionlabs.ffmlibrary.annotations

/**
 * Annotation für die Markierung einer Klasse als Teil einer nativen Bibliothek
 *
 * @param[className] Der Name der Klasse, die die nativen Funktionen enthält.
 * @param[libraryName] Der Name der nativen Bibliothek, die geladen werden soll.
 * @param[containsLinker] Gibt an, ob die Klasse einen Linker enthält, der die nativen Funktionen aufruft. Standardmäßig: False.
 * @param[externalLinker] Der Name einer externen Klasse, die als Linker fungiert, wenn [containsLinker] auf false gesetzt ist. Standardmäßig leer.
 * @param[isCClass] Gibt an, ob die Klasse eine C-Klasse repräsentiert. Muss zusammen mit [NativeFunction.selfReferencing] verwendet werden. Standardmäßig: False.
 * @param[platform] Gibt die Plattform-Architektur der nativen Bibliothek an. Standardmäßig leer.
 * @param[path] Gibt den Pfad zur nativen Bibliothek (.dll, .so, etc.) an, wenn [containsLinker] auf false gesetzt ist. Standardmäßig leer.
 */
@Target(AnnotationTarget.CLASS)
annotation class NativeClass(
    val className: String,
    val libraryName: String,
    val containsLinker: Boolean = false,
    val externalLinker: String = "",
    val isCClass: Boolean = false,
    val platform: String = "",
    val path: String = ""
)