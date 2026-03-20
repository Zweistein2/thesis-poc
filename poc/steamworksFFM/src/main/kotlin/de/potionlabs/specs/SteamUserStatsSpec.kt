package de.potionlabs.specs

import de.potionlabs.ffmlibrary.annotations.NativeFunction
import de.potionlabs.ffmlibrary.annotations.NativeClass

@NativeClass(
    className = "SteamUserStats",
    libraryName = "Steamworks",
    externalLinker = "de.potionlabs.SteamworksApi",
    platform = "Win64",
    isCClass = true)
interface SteamUserStatsSpec {
    @NativeFunction("SteamAPI_ISteamUserStats_GetNumAchievements", true)
    fun getNumAchievements(): Int
    // uint32 SteamAPI_ISteamUserStats_GetNumAchievements( ISteamUserStats* self );
}
