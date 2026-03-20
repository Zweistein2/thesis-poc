package de.potionlabs.ffmlibrary.annotations

import de.potionlabs.ffmlibrary.utils.Datatype

/**
 * Annotation für die Markierung eines Parameters einer nativen Funktion, welcher ein Zeiger auf ein Datenelement ist. Muss innerhalb einer [NativeFunction]-annotierten Methode verwendet werden.
 *
 * @param[name] Gibt den Namen des Parameters an.
 * @param[type] Gibt den Datentyp des Parameters als [Datatype] an.
 * @param[priority] Gibt an, an welcher Stelle in der nativen Funktion der Parameter stehen soll, beginnend ab 0.
 * @param[value] Legt einen Standard-Wert (bspw. die Größenangabe für ein Array) für den Parameter fest, der der nativen Funktion übergeben wird. Muss dem [type] entsprechen.
 */
@Repeatable
@Target(AnnotationTarget.FUNCTION)
annotation class NativeFunctionPointerParam(
    val name: String,
    val type: Datatype,
    val priority: Int,
    val value: String
)