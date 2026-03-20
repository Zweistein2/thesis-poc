package de.potionlabs.ffmlibrary.annotations

/**
 * Annotation für die Markierung eines Parameters einer nativen Funktion. Muss innerhalb einer [NativeFunction]-annotierten Methode verwendet werden.
 *
 * @param[priority] Gibt an, an welcher Stelle in der nativen Funktion der Parameter stehen soll, beginnend ab 0.
 * @param[isCallbackId] Gibt an, ob es sich bei dem Parameter um die ID eines Callbacks handelt, sollte die Funktion unterschiedliche Callbacks zurückgeben können. Standardmäßig: False.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class NativeFunctionParam(
    val priority: Int,
    val isCallbackId: Boolean = false
)