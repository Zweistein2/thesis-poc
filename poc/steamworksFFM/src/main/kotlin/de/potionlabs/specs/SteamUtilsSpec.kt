package de.potionlabs.specs

import de.potionlabs.ffmlibrary.annotations.NativeFunction
import de.potionlabs.ffmlibrary.annotations.NativeFunctionParam
import de.potionlabs.ffmlibrary.annotations.NativeFunctionPointerParam
import de.potionlabs.ffmlibrary.annotations.NativeFunctionPointerStructParam
import de.potionlabs.ffmlibrary.annotations.NativeFunctionReturnType
import de.potionlabs.ffmlibrary.annotations.NativeClass
import de.potionlabs.ffmlibrary.utils.Datatype
import de.potionlabs.ffmlibrary.utils.ReturnTuple
import de.potionlabs.ffmlibrary.utils.Struct

@NativeClass(
    className = "SteamUtils",
    libraryName = "Steamworks",
    externalLinker = "de.potionlabs.SteamworksApi",
    platform = "Win64",
    isCClass = true)
interface SteamUtilsSpec {
    @NativeFunction("SteamAPI_ISteamUtils_GetAppID", true)
    fun getAppID(): Int
    // uint32 SteamAPI_ISteamUtils_GetAppID( ISteamUtils* self );

    @NativeFunction("SteamAPI_ISteamUtils_IsAPICallCompleted", true)
    @NativeFunctionReturnType(Datatype.BOOLEAN)
    @NativeFunctionPointerParam("pbFailed", Datatype.BOOLEAN, 1, "false")
    fun isAPICallCompleted(
        @NativeFunctionParam(0) hSteamAPICall: Long
    ): ReturnTuple
    // bool SteamAPI_ISteamUtils_IsAPICallCompleted( ISteamUtils* self, SteamAPICall_t hSteamAPICall, bool * pbFailed );

    @NativeFunction("SteamAPI_ISteamUtils_GetAPICallFailureReason", true)
    fun getAPICallFailureReason(
        @NativeFunctionParam(0) hSteamAPICall: Long
    ): Int
    // ESteamAPICallFailure SteamAPI_ISteamUtils_GetAPICallFailureReason( ISteamUtils* self, SteamAPICall_t hSteamAPICall );

    @NativeFunction("SteamAPI_ISteamUtils_GetAPICallResult", true)
    @NativeFunctionReturnType(Datatype.BOOLEAN)
    @NativeFunctionPointerStructParam("pCallback", Struct::class, 1, false)
    @NativeFunctionPointerParam("pbFailed", Datatype.BOOLEAN, 4, "false")
    fun getAPICallResult(
        @NativeFunctionParam(0) hSteamAPICall: Long,
        @NativeFunctionParam(2) cubCallback: Int,
        @NativeFunctionParam(3, true) iCallbackExpected: Int
    ): ReturnTuple
    // bool SteamAPI_ISteamUtils_GetAPICallResult( ISteamUtils* self, SteamAPICall_t hSteamAPICall, void * pCallback, int cubCallback, int iCallbackExpected, bool * pbFailed );
}
