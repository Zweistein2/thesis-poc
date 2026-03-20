package de.potionlabs.specs

import de.potionlabs.ffmlibrary.annotations.NativeFunction
import de.potionlabs.ffmlibrary.annotations.NativeFunctionParam
import de.potionlabs.ffmlibrary.annotations.NativeClass

@NativeClass(
    className = "SteamApps",
    libraryName = "Steamworks",
    externalLinker = "de.potionlabs.SteamworksApi",
    platform = "Win64",
    isCClass = true
)
interface SteamAppsSpec {
    @NativeFunction("SteamAPI_ISteamApps_BIsSubscribed", true)
    fun isSubscribed(): Boolean
    // bool SteamAPI_ISteamApps_BIsSubscribed( ISteamApps* self );

    @NativeFunction("SteamAPI_ISteamApps_BIsLowViolence", true)
    fun isLowViolence(): Boolean
    // bool SteamAPI_ISteamApps_BIsLowViolence( ISteamApps* self );

    @NativeFunction("SteamAPI_ISteamApps_BIsCybercafe", true)
    fun isCybercafe(): Boolean
    // bool SteamAPI_ISteamApps_BIsCybercafe( ISteamApps* self );

    @NativeFunction("SteamAPI_ISteamApps_BIsVACBanned", true)
    fun isVACBanned(): Boolean
    // bool SteamAPI_ISteamApps_BIsVACBanned( ISteamApps* self );

    @NativeFunction("SteamAPI_ISteamApps_GetCurrentGameLanguage", true)
    fun getCurrentGameLanguage(): String
    // const char * SteamAPI_ISteamApps_GetCurrentGameLanguage( ISteamApps* self );

    @NativeFunction("SteamAPI_ISteamApps_GetAvailableGameLanguages", true)
    fun getAvailableGameLanguages(): String
    // const char * SteamAPI_ISteamApps_GetAvailableGameLanguages( ISteamApps* self );

    @NativeFunction("SteamAPI_ISteamApps_BIsSubscribedApp", true)
    fun isSubscribedApp(
        @NativeFunctionParam(0) appID: Int
    ): Boolean
    // bool SteamAPI_ISteamApps_BIsSubscribedApp( ISteamApps* self, AppId_t appID );

    @NativeFunction("SteamAPI_ISteamApps_BIsDlcInstalled", true)
    fun isDlcInstalled(
        @NativeFunctionParam(0) appID: Int
    ): Boolean
    // bool SteamAPI_ISteamApps_BIsDlcInstalled( ISteamApps* self, AppId_t appID );

    @NativeFunction("SteamAPI_ISteamApps_BIsSubscribedFromFreeWeekend", true)
    fun isSubscribedFromFreeWeekend(): Boolean
    // bool SteamAPI_ISteamApps_BIsSubscribedFromFreeWeekend( ISteamApps* self );

    @NativeFunction("SteamAPI_ISteamApps_GetDLCCount", true)
    fun getDLCCount(): Int
    // int SteamAPI_ISteamApps_GetDLCCount( ISteamApps* self );

    @NativeFunction("SteamAPI_ISteamApps_GetCurrentBetaName", true)
    fun getCurrentBetaName(
        @NativeFunctionParam(0) pchName: String,
        @NativeFunctionParam(1) cchNameBufferSize: Int
    ): Boolean
    // bool SteamAPI_ISteamApps_GetCurrentBetaName( ISteamApps* self, char * pchName, int cchNameBufferSize );

    @NativeFunction("SteamAPI_ISteamApps_MarkContentCorrupt", true)
    fun markContentCorrupt(
        @NativeFunctionParam(0) bMissingFilesOnly: Boolean
    ): Boolean
    // bool SteamAPI_ISteamApps_MarkContentCorrupt( ISteamApps* self, bool bMissingFilesOnly );

    @NativeFunction("SteamAPI_ISteamApps_RequestAllProofOfPurchaseKeys", true)
    fun requestAllProofOfPurchaseKeys()
    // void SteamAPI_ISteamApps_RequestAllProofOfPurchaseKeys( ISteamApps* self );

    @NativeFunction("SteamAPI_ISteamApps_GetAppBuildId", true)
    fun getAppBuildId(): Int
    // int SteamAPI_ISteamApps_GetAppBuildId( ISteamApps* self );

    @NativeFunction("SteamAPI_ISteamApps_BIsAppInstalled", true)
    fun isAppInstalled(
        @NativeFunctionParam(0) appID: Int
    ): Boolean
    // bool SteamAPI_ISteamApps_BIsAppInstalled( ISteamApps* self, AppId_t appID );

    @NativeFunction("SteamAPI_ISteamApps_BIsSubscribedFromFamilySharing", true)
    fun isSubscribedFromFamilySharing(): Boolean
    // bool SteamAPI_ISteamApps_BIsSubscribedFromFamilySharing( ISteamApps* self );

    @NativeFunction("SteamAPI_ISteamApps_BIsTimedTrial", true)
    fun isTimedTrial(
        @NativeFunctionParam(0) punSecondsAllowed: Int,
        @NativeFunctionParam(1) punSecondsPlayed: Int
    ): Boolean
    // bool SteamAPI_ISteamApps_BIsTimedTrial( ISteamApps* self, uint32 * punSecondsAllowed, uint32 * punSecondsPlayed );

    @NativeFunction("SteamAPI_ISteamApps_SetDlcContext", true)
    fun setDlcContext(
        @NativeFunctionParam(0) nAppID: Int
    ): Boolean
    // bool SteamAPI_ISteamApps_SetDlcContext( ISteamApps* self, AppId_t nAppID );
}