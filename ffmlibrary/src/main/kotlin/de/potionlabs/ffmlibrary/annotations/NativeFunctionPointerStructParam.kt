package de.potionlabs.ffmlibrary.annotations

import de.potionlabs.ffmlibrary.utils.Struct
import kotlin.reflect.KClass

/**
 * Annotation für die Markierung eines Parameters einer nativen Funktion, welcher ein Zeiger auf eine Struktur ist. Muss innerhalb einer [NativeFunction]-annotierten Methode verwendet werden.
 *
 * @param[name] Gibt den Namen des Parameters an.
 * @param[type] Gibt den Strukturtyp an. Die Struktur muss manuell implementiert werden und dabei das Interface [Struct] einbinden.
 * @param[priority] Gibt an, an welcher Stelle in der nativen Funktion der Parameter stehen soll, beginnend ab 0.
 * @param[isSequence] Gibt an, ob der Zeiger auf eine einzelne Struktur oder eine Liste an Strukturen zeigt. Standardmäßig: False.
 * @param[sequenceSize] Gibt an, wie viele Elemente in der Liste enthalten sind. Standardmäßig: 1.
 */
@Repeatable
@Target(AnnotationTarget.FUNCTION)
annotation class NativeFunctionPointerStructParam(
    val name: String,
    val type: KClass<out Struct>,
    val priority: Int,
    val isSequence: Boolean = false,
    val sequenceSize: String = "1"
)