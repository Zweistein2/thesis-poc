package de.potionlabs.tuples

import de.potionlabs.ffmlibrary.annotations.NativeTuple
import de.potionlabs.ffmlibrary.utils.ReturnTuple

@NativeTuple(
    libraryName = "Steamworks",
    className = "SteamAPI"
)
data class InitFlatReturnTuple(
    val invocationResult: Int,
    val pOutErrMsgValue: String,
) : ReturnTuple