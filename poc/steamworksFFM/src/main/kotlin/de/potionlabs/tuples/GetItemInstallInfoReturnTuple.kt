package de.potionlabs.tuples

import de.potionlabs.ffmlibrary.annotations.NativeTuple
import de.potionlabs.ffmlibrary.utils.ReturnTuple

@NativeTuple(
    libraryName = "Steamworks",
    className = "SteamUGC"
)
data class GetItemInstallInfoReturnTuple(
    val invocationResult: Boolean,
    val punSizeOnDisk: Long,
    val punTimeStamp: Int,
    val pchFolder: String
) : ReturnTuple