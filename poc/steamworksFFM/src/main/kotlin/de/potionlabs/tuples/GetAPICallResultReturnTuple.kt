package de.potionlabs.tuples

import de.potionlabs.ffmlibrary.annotations.NativeTuple
import de.potionlabs.ffmlibrary.utils.ReturnTuple
import de.potionlabs.ffmlibrary.utils.Struct

@NativeTuple(
    libraryName = "Steamworks",
    className = "SteamUtils"
)
data class GetAPICallResultReturnTuple(
    val result: Boolean,
    val pbFailed: Boolean,
    val pCallback: Struct
) : ReturnTuple
