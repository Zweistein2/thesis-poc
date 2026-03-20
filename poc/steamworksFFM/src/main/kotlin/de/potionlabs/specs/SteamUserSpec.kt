package de.potionlabs.specs

import de.potionlabs.ffmlibrary.annotations.NativeFunction
import de.potionlabs.ffmlibrary.annotations.NativeClass

@NativeClass(
    className = "SteamUser",
    libraryName = "Steamworks",
    externalLinker = "de.potionlabs.SteamworksApi",
    platform = "Win64",
    isCClass = true)
interface SteamUserSpec {
    @NativeFunction("SteamAPI_ISteamUser_BLoggedOn", true)
    fun loggedOn(): Boolean
    // bool SteamAPI_ISteamUser_BLoggedOn( ISteamUser* self );

    @NativeFunction("SteamAPI_ISteamUser_GetSteamID", true)
    fun getSteamID(): Long
    // uint64_steamid SteamAPI_ISteamUser_GetSteamID( ISteamUser* self );

    @NativeFunction("SteamAPI_ISteamUser_BIsBehindNAT", true)
    fun isBehindNAT(): Boolean
    // bool SteamAPI_ISteamUser_BIsBehindNAT( ISteamUser* self );

    @NativeFunction("SteamAPI_ISteamUser_BIsPhoneVerified", true)
    fun isPhoneVerified(): Boolean
    // bool SteamAPI_ISteamUser_BIsPhoneVerified( ISteamUser* self );

    @NativeFunction("SteamAPI_ISteamUser_BIsTwoFactorEnabled", true)
    fun isTwoFactorEnabled(): Boolean
    // bool SteamAPI_ISteamUser_BIsTwoFactorEnabled( ISteamUser* self );

    @NativeFunction("SteamAPI_ISteamUser_BIsPhoneIdentifying", true)
    fun isPhoneIdentifying(): Boolean
    // bool SteamAPI_ISteamUser_BIsPhoneIdentifying( ISteamUser* self );

    @NativeFunction("SteamAPI_ISteamUser_BIsPhoneRequiringVerification", true)
    fun isPhoneRequiringVerification(): Boolean
    // bool SteamAPI_ISteamUser_BIsPhoneRequiringVerification( ISteamUser* self );
}