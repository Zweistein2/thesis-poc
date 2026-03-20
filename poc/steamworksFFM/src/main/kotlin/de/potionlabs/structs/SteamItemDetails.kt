package de.potionlabs.structs

import de.potionlabs.ffmlibrary.annotations.NativeStruct
import de.potionlabs.ffmlibrary.utils.Struct

@NativeStruct("Steamworks")
data class SteamItemDetails(
    val itemId: Long,
    val definitionId: Int,
    val quantity: Short,
    val flags: Short
) : Struct
