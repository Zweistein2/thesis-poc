package de.potionlabs.ffmlibrary.structure

/**
 * Diese Klasse repräsentiert ein mit [de.potionlabs.ffmlibrary.annotations.NativeTuple] annotiertes [de.potionlabs.ffmlibrary.utils.ReturnTuple] der nativen Bibliothek.
 * Es wird genutzt, um mehrere Rückgabewerte von nativen Methoden zu repräsentieren, welche in einem einzigen Objekt zurückgegeben werden.
 * Dies ist notwendig, da Zeiger-Parameter aufgrund der "Call-by-Value"-Semantik von Java und Kotlin nach einem Funktionsaufruf nicht die korrekten Werte beinhalten.
 * Stattdessen sind deren Werte dieselben, wie vor dem Aufruf der nativen Funktion.
 *
 * @param[libraryName] Der Name der nativen Bibliothek, zu welcher das [de.potionlabs.ffmlibrary.utils.ReturnTuple] gehört.
 * @param[fileName] Der Name der Datei, in welcher das [de.potionlabs.ffmlibrary.utils.ReturnTuple] generiert wird.
 * @param[className] Der Name der Klasse, welche das [de.potionlabs.ffmlibrary.utils.ReturnTuple] repräsentiert.
 * @param[packageName] Der Name des Packages, in welchem das [de.potionlabs.ffmlibrary.utils.ReturnTuple] generiert wird.
 */
data class FFMTuple(
    val libraryName: String,
    val className: String,
    val fileName: String,
    val packageName: String,
): FFMObject
