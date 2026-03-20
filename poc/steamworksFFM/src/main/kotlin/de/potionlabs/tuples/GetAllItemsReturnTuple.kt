package de.potionlabs.tuples

import de.potionlabs.ffmlibrary.annotations.NativeTuple
import de.potionlabs.ffmlibrary.utils.ReturnTuple

@NativeTuple(
    libraryName = "Steamworks",
    className = "SteamInventory"
)
data class GetAllItemsReturnTuple(
    val invocationResult: Boolean,
    val pResultHandleValue: Int,
) : ReturnTuple