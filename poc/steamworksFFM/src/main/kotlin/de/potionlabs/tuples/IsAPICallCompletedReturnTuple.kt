package de.potionlabs.tuples

import de.potionlabs.ffmlibrary.annotations.NativeTuple
import de.potionlabs.ffmlibrary.utils.ReturnTuple

@NativeTuple(
    libraryName = "Steamworks",
    className = "SteamUtils"
)
data class IsAPICallCompletedReturnTuple(
    val result: Boolean,
    val pbFailed: Boolean,
) : ReturnTuple
