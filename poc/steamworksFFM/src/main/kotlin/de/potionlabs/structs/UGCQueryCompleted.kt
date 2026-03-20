package de.potionlabs.structs

import de.potionlabs.ffmlibrary.annotations.NativeStruct
import de.potionlabs.ffmlibrary.annotations.NativeStructStringSize
import de.potionlabs.ffmlibrary.utils.Struct

@NativeStruct("Steamworks", 3401)
data class UGCQueryCompleted(
    val m_handle: Long,
    val m_eResult: Int,
    val m_unNumResultsReturned: Int,
    val m_unTotalMatchingResults: Int,
    val m_bCachedData: Boolean,
    @NativeStructStringSize(256)
    val m_rgchNextCursor: String
) : Struct