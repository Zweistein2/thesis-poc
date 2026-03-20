package de.potionlabs.ffmlibrary.annotations

/**
 * Annotation für die Markierung einer Methode als NativeFunction. Muss innerhalb einer [NativeClass]-annotierten Klasse verwendet werden.
 *
 * @param[name] Gibt den Namen der nativen Funktion an, die aufgerufen werden soll.
 * @param[selfReferencing] Gibt an, ob eine Referenz auf die Klasse, in welchem die Methode implementiert ist, übergeben werden soll. Muss zusammen mit [NativeClass.isCClass] verwendet werden. Standardmäßig: False.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class NativeFunction(
    val name: String,
    val selfReferencing: Boolean = false
)