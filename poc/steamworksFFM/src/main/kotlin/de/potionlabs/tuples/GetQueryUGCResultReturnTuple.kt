package de.potionlabs.tuples

import de.potionlabs.structs.SteamUGCDetails
import de.potionlabs.ffmlibrary.annotations.NativeTuple
import de.potionlabs.ffmlibrary.utils.ReturnTuple

@NativeTuple(
    libraryName = "Steamworks",
    className = "SteamUGC"
)
data class GetQueryUGCResultReturnTuple(
    val result: Boolean,
    val pDetails: SteamUGCDetails
) : ReturnTuple
