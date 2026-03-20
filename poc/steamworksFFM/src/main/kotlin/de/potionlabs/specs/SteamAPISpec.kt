package de.potionlabs.specs

import de.potionlabs.structs.CallbackMsg
import de.potionlabs.ffmlibrary.annotations.*
import de.potionlabs.ffmlibrary.utils.Datatype
import de.potionlabs.ffmlibrary.utils.ReturnTuple
import de.potionlabs.ffmlibrary.utils.Struct

@NativeClass(
    className = "SteamAPI",
    libraryName = "Steamworks",
    externalLinker = "de.potionlabs.SteamworksApi",
    platform = "Win64"
)
interface SteamAPISpec {
    @NativeFunction("SteamAPI_InitFlat")
    @NativeFunctionReturnType(Datatype.INTEGER)
    @NativeFunctionPointerStringParam("pOutErrMsg", Datatype.STRING, 0, 1024)
    fun initFlat(): ReturnTuple
    // ESteamAPIInitResult SteamAPI_InitFlat( SteamErrMsg *pOutErrMsg );

    @NativeFunction("SteamAPI_Shutdown")
    fun shutdown()
    // void SteamAPI_Shutdown();

    @NativeFunction("SteamAPI_RestartAppIfNecessary")
    fun restartAppIfNecessary(@NativeFunctionParam(0) unOwnAppID: Int): Boolean
    // bool SteamAPI_RestartAppIfNecessary( uint32 unOwnAppID );

    @NativeFunction("SteamAPI_ReleaseCurrentThreadMemory")
    fun releaseCurrentThreadMemory()
    // void SteamAPI_ReleaseCurrentThreadMemory();

    @NativeFunction("SteamAPI_WriteMiniDump")
    fun writeMiniDump(
        @NativeFunctionParam(0) uStructuredExceptionCode: Int,
        @NativeFunctionParam(1) pvExceptionInfo: Long,
        @NativeFunctionParam(2) uBuildID: Int
    )
    // void SteamAPI_WriteMiniDump( uint32 uStructuredExceptionCode, void* pvExceptionInfo, uint32 uBuildID );

    @NativeFunction("SteamAPI_SetMiniDumpComment")
    fun setMiniDumpComment(@NativeFunctionParam(0) pchMsg: String)
    // void SteamAPI_SetMiniDumpComment( const char *pchMsg );

    @NativeFunction("SteamAPI_IsSteamRunning")
    fun isSteamRunning(): Boolean
    // bool SteamAPI_IsSteamRunning();

    @NativeFunction("SteamAPI_GetSteamInstallPath")
    fun getSteamInstallPath(): String
    // const char *SteamAPI_GetSteamInstallPath();

    @NativeFunction("SteamAPI_SetTryCatchCallbacks")
    fun setTryCatchCallbacks(@NativeFunctionParam(0) bTryCatchCallbacks: Boolean)
    // void SteamAPI_SetTryCatchCallbacks( bool bTryCatchCallbacks );

    @NativeFunction("SteamAPI_RunCallbacks")
    fun runCallbacks()
    // void SteamAPI_RunCallbacks();

    @NativeFunction("SteamAPI_GetHSteamPipe")
    fun getHSteamPipe(): Int
    // HSteamPipe SteamAPI_GetHSteamPipe()

    @NativeFunction("SteamAPI_GetHSteamUser")
    fun getHSteamUser(): Int
    // HSteamUser SteamAPI_GetHSteamUser()

    // --------------- Manual Callback Dispatches ---------------
    @NativeFunction("SteamAPI_ManualDispatch_Init")
    fun manualDispatchInit()
    // void SteamAPI_ManualDispatch_Init();

    @NativeFunction("SteamAPI_ManualDispatch_RunFrame")
    fun manualDispatchRunFrame(
        @NativeFunctionParam(0) hSteamPipe: Int
    )
    // void SteamAPI_ManualDispatch_RunFrame( HSteamPipe hSteamPipe );

    @NativeFunction("SteamAPI_ManualDispatch_GetNextCallback")
    @NativeFunctionReturnType(Datatype.BOOLEAN)
    @NativeFunctionPointerStructParam("pCallbackMsg", CallbackMsg::class, 1)
    fun manualDispatchGetNextCallback(
        @NativeFunctionParam(0) hSteamPipe: Int
    ): ReturnTuple
    // bool SteamAPI_ManualDispatch_GetNextCallback( HSteamPipe hSteamPipe, CallbackMsg_t *pCallbackMsg );

    @NativeFunction("SteamAPI_ManualDispatch_FreeLastCallback")
    fun manualDispatchFreeLastCallback(
        @NativeFunctionParam(0) hSteamPipe: Int
    )
    // void SteamAPI_ManualDispatch_FreeLastCallback( HSteamPipe hSteamPipe );

    @NativeFunction("SteamAPI_ManualDispatch_GetAPICallResult")
    @NativeFunctionReturnType(Datatype.BOOLEAN)
    @NativeFunctionPointerStructParam("pCallback", Struct::class, 2)
    @NativeFunctionPointerParam("pbFailed", Datatype.BOOLEAN, 5, "false")
    fun manualDispatchGetAPICallResult(
        @NativeFunctionParam(0) hSteamPipe: Int,
        @NativeFunctionParam(1) hSteamAPICall: Long,
        @NativeFunctionParam(3) cubCallback: Int,
        @NativeFunctionParam(4, true) iCallbackExpected: Int
    ): ReturnTuple
    // bool SteamAPI_ManualDispatch_GetAPICallResult( HSteamPipe hSteamPipe, SteamAPICall_t hSteamAPICall, void *pCallback, int cubCallback, int iCallbackExpected, bool *pbFailed );

    // --------------- Other Interfaces ---------------
    @NativeFunction("SteamAPI_SteamUser_v023")
    fun getSteamUser(): SteamUserSpec

    @NativeFunction("SteamAPI_SteamFriends_v018")
    fun getSteamFriends(): SteamFriendsSpec

    @NativeFunction("SteamAPI_SteamUtils_v010")
    fun getSteamUtils(): SteamUtilsSpec

    @NativeFunction("SteamAPI_SteamGameServerUtils_v010")
    fun getSteamGameServerUtils(): SteamUtilsSpec

    @NativeFunction("SteamAPI_SteamRemoteStorage_v016")
    fun getSteamRemoteStorage(): SteamRemoteStorageSpec

    @NativeFunction("SteamAPI_SteamUserStats_v013")
    fun getSteamUserStats(): SteamUserStatsSpec

    @NativeFunction("SteamAPI_SteamApps_v008")
    fun getSteamApps(): SteamAppsSpec

    @NativeFunction("SteamAPI_SteamUGC_v021")
    fun getSteamUGC(): SteamUGCSpec

    @NativeFunction("SteamAPI_SteamGameServerUGC_v021")
    fun getSteamGameServerUGC(): SteamUGCSpec

    @NativeFunction("SteamAPI_SteamInventory_v003")
    fun getSteamInventory(): SteamInventorySpec

    @NativeFunction("SteamAPI_SteamGameServerInventory_v003")
    fun getSteamGameServerInventory(): SteamInventorySpec
}