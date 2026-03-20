package de.potionlabs.ffmlibrary.annotations

/**
 * Annotiert eine Klasse als "RückgabeTupel" für eine native Funktion. Muss verwendet werden, wenn eine native Funktion einen Rückgabewert enthält und zusätzlich Zeiger bekommt, deren Wert innerhalb der Funktion geändert wird.
 *
 * @param[libraryName] Der Name der nativen Bibliothek, zu welcher das "RückgabeTupel" gehört.
 * @param[className] Der Name der [NativeClass]-annotierten Klasse, in der das "RückgabeTupel" innerhalb einer [NativeFunction]-annotierten Methode als Rückgabewert verwendet wird. Es ist dort notwendig für die korrekte Generierung der Imports.
 */
@Target(AnnotationTarget.CLASS)
annotation class NativeTuple(
    val libraryName: String,
    val className: String
)