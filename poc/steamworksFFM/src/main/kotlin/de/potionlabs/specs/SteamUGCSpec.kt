package de.potionlabs.specs

import de.potionlabs.structs.SteamUGCDetails
import de.potionlabs.ffmlibrary.annotations.NativeFunction
import de.potionlabs.ffmlibrary.annotations.NativeFunctionParam
import de.potionlabs.ffmlibrary.annotations.NativeFunctionPointerParam
import de.potionlabs.ffmlibrary.annotations.NativeFunctionPointerStringParam
import de.potionlabs.ffmlibrary.annotations.NativeFunctionPointerStructParam
import de.potionlabs.ffmlibrary.annotations.NativeFunctionReturnType
import de.potionlabs.ffmlibrary.annotations.NativeClass
import de.potionlabs.ffmlibrary.utils.Datatype
import de.potionlabs.ffmlibrary.utils.ReturnTuple

@NativeClass(
    className = "SteamUGC",
    libraryName = "Steamworks",
    externalLinker = "de.potionlabs.SteamworksApi",
    platform = "Win64",
    isCClass = true
)
interface SteamUGCSpec {
    @NativeFunction("SteamAPI_ISteamUGC_CreateQueryUserUGCRequest", selfReferencing = true)
    fun createQueryUserUGCRequest(
        @NativeFunctionParam(0) unAccountID: Int,
        @NativeFunctionParam(1) eListType: Int,
        @NativeFunctionParam(2) eMatchingUGCType: Int,
        @NativeFunctionParam(3) eSortOrder: Int,
        @NativeFunctionParam(4) nCreatorAppID: Int,
        @NativeFunctionParam(5) nConsumerAppID: Int,
        @NativeFunctionParam(6) unPage: Int
    ): Long
    // UGCQueryHandle_t SteamAPI_ISteamUGC_CreateQueryUserUGCRequest( ISteamUGC* self, AccountID_t unAccountID, EUserUGCList eListType, EUGCMatchingUGCType eMatchingUGCType, EUserUGCListSortOrder eSortOrder, AppId_t nCreatorAppID, AppId_t nConsumerAppID, uint32 unPage );

    @NativeFunction("SteamAPI_ISteamUGC_SendQueryUGCRequest", selfReferencing = true)
    fun sendQueryUGCRequest(
        @NativeFunctionParam(0) handle: Long
    ): Long
    // SteamAPICall_t SteamAPI_ISteamUGC_SendQueryUGCRequest( ISteamUGC* self, UGCQueryHandle_t handle );

    @NativeFunction("SteamAPI_ISteamUGC_GetQueryUGCResult", selfReferencing = true)
    @NativeFunctionReturnType(Datatype.BOOLEAN)
    @NativeFunctionPointerStructParam("pDetails", SteamUGCDetails::class, 2)
    fun getQueryUGCResult(
        @NativeFunctionParam(0) handle: Long,
        @NativeFunctionParam(1) index: Int
    ): ReturnTuple
    // bool SteamAPI_ISteamUGC_GetQueryUGCResult( ISteamUGC* self, UGCQueryHandle_t handle, uint32 index, SteamUGCDetails_t * pDetails );

    @NativeFunction("SteamAPI_ISteamUGC_ReleaseQueryUGCRequest", selfReferencing = true)
    fun releaseQueryUGCRequest(
        @NativeFunctionParam(0) handle: Long
    ): Boolean
    // bool SteamAPI_ISteamUGC_ReleaseQueryUGCRequest( ISteamUGC* self, UGCQueryHandle_t handle );

    @NativeFunction("SteamAPI_ISteamUGC_GetNumSubscribedItems", true)
    fun getNumSubscribedItems(): Int
    // uint32 SteamAPI_ISteamUGC_GetNumSubscribedItems( ISteamUGC* self );

    @NativeFunction("SteamAPI_ISteamUGC_GetItemInstallInfo", true)
    @NativeFunctionReturnType(Datatype.BOOLEAN)
    @NativeFunctionPointerParam("punSizeOnDisk", Datatype.LONG, 1, "0L")
    @NativeFunctionPointerStringParam("pchFolder", Datatype.STRING, 2, 1024)
    @NativeFunctionPointerParam("punTimeStamp", Datatype.INTEGER, 4, "0")
    fun getItemInstallInfo(
        @NativeFunctionParam(0) nPublishedFileID: Long,
        @NativeFunctionParam(3) cchFolderSize: Int,
    ): ReturnTuple
    // bool SteamAPI_ISteamUGC_GetItemInstallInfo( ISteamUGC* self, PublishedFileId_t nPublishedFileID, uint64 * punSizeOnDisk, char * pchFolder, uint32 cchFolderSize, uint32 * punTimeStamp );

    @NativeFunction("SteamAPI_ISteamUGC_DownloadItem", true)
    fun downloadItem(
        @NativeFunctionParam(0) nPublishedFileID: Long,
        @NativeFunctionParam(1) bHighPriority: Boolean
    ): Boolean
    // bool SteamAPI_ISteamUGC_DownloadItem( ISteamUGC* self, PublishedFileId_t nPublishedFileID, bool bHighPriority );
}
