package de.potionlabs.ffmlibrary.structure

import com.google.devtools.ksp.symbol.KSType

/**
 * Diese Klasse repräsentiert ein Attribut einer nativen Struktur.
 *
 * @param[fieldName] Der Name des Attributs.
 * @param[fieldType] Der Typ des Attributs.
 * @param[stringSize] Die Größe des Attributs, falls es sich um einen String handelt. Ansonsten ist dieser Wert null.
 * @param[padding] Die Anzahl der Padding-Bytes, welche vor diesem Attribut hinzugefügt werden müssen, um die richtige Ausrichtung innerhalb der Struktur zu gewährleisten. Dieser Wert wird in der "calculatePadding"-Methode des [FFMStruct] berechnet.
 */
data class FFMStructField(
    val fieldName: String,
    val fieldType: KSType,
    val stringSize: Long?,
    var padding: Long = 0
): FFMObject
