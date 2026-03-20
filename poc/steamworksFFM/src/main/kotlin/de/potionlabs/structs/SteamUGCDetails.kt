package de.potionlabs.structs

import de.potionlabs.ffmlibrary.annotations.NativeStruct
import de.potionlabs.ffmlibrary.annotations.NativeStructStringSize
import de.potionlabs.ffmlibrary.utils.Struct

@NativeStruct("Steamworks")
data class SteamUGCDetails(
    val m_nPublishedFileId: Long,
    val m_eResult: Int,
    val m_eFileType: Int,
    val m_nCreatorAppID: Int,
    val m_nConsumerAppID: Int,
    @NativeStructStringSize(129)
    val m_rgchTitle: String,
    @NativeStructStringSize(8000)
    val m_rgchDescription: String,
    val m_ulSteamIDOwner: Long,
    val m_rtimeCreated: Int,
    val m_rtimeUpdated: Int,
    val m_rtimeAddedToUserList: Int,
    val m_eVisibility: Int,
    val m_bBanned: Boolean,
    val m_bAcceptedForUse: Boolean,
    val m_bTagsTruncated: Boolean,
    @NativeStructStringSize(1025)
    val m_rgchTags: String,
    val m_hFile: Long,
    val m_hPreviewFile: Long,
    @NativeStructStringSize(260)
    val m_pchFileName: String,
    val m_nFileSize: Int,
    val m_nPreviewFileSize: Int,
    @NativeStructStringSize(256)
    val m_rgchURL: String,
    val m_unVotesUp: Int,
    val m_unVotesDown: Int,
    val m_flScore: Float,
    val m_unNumChildren: Int,
    val m_ulTotalFilesSize: Long
) : Struct