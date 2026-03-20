package de.potionlabs.ffmlibrary.annotations

import de.potionlabs.ffmlibrary.utils.Datatype

/**
 * Annotation für die Markierung eines Parameters einer nativen Funktion, welcher ein Zeiger auf eine Zeichenkette ist. Muss innerhalb einer [NativeFunction]-annotierten Methode verwendet werden.
 *
 * @param[name] Gibt den Namen des Parameters an.
 * @param[type] Muss hier explizit angegeben werden, damit der KSP-Generator die Klasse korrekt auslesen kann. Ist immer [Datatype.STRING].
 * @param[priority] Gibt an, an welcher Stelle in der nativen Funktion der Parameter stehen soll, beginnend ab 0.
 * @param[size] Legt die Länge der Zeichenkette fest.
 */
@Repeatable
@Target(AnnotationTarget.FUNCTION)
annotation class NativeFunctionPointerStringParam(
    val name: String,
    val type: Datatype,
    val priority: Int,
    val size: Int
)