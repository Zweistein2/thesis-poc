package de.potionlabs.tuples

import de.potionlabs.structs.SteamItemDetails
import de.potionlabs.ffmlibrary.annotations.NativeTuple
import de.potionlabs.ffmlibrary.utils.ReturnTuple

@NativeTuple(
    libraryName = "Steamworks",
    className = "SteamInventory"
)
data class GetResultItemsReturnTuple(
    val invocationResult: Boolean,
    val punOutItemsArraySizeValue: Int,
    val pOutItemsArrayValue: List<SteamItemDetails>,
) : ReturnTuple