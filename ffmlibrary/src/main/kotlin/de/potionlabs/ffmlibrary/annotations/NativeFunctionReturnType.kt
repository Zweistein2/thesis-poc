package de.potionlabs.ffmlibrary.annotations

import de.potionlabs.ffmlibrary.utils.Datatype

/**
 * Annotation für die Markierung des Rückgabetyps einer nativen Funktion. Muss innerhalb einer [NativeFunction]-annotierten Methode verwendet werden.
 * Muss nur dann genutzt werden, wenn ein [de.potionlabs.ffmlibrary.utils.ReturnTuple] zurückgegeben wird, da der Generator den Rückgabetyp dann nicht selbstständig auswerten kann.
 *
 * @param[type] Gibt den Datentyp des Rückgabewerts als [Datatype] an.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class NativeFunctionReturnType(
    val type: Datatype,
)