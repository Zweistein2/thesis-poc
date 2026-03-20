package de.potionlabs.specs

import de.potionlabs.ffmlibrary.annotations.NativeFunction
import de.potionlabs.ffmlibrary.annotations.NativeClass

@NativeClass(
    className = "SteamFriends",
    libraryName = "Steamworks",
    externalLinker = "de.potionlabs.SteamworksApi",
    platform = "Win64",
    isCClass = true
)
interface SteamFriendsSpec {
    @NativeFunction("SteamAPI_ISteamFriends_GetPersonaName", true)
    fun getPersonaName(): String
    // const char * SteamAPI_ISteamFriends_GetPersonaName( ISteamFriends* self );
}
