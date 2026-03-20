package de.potionlabs.specs

import de.potionlabs.ffmlibrary.annotations.NativeFunction
import de.potionlabs.ffmlibrary.annotations.NativeClass

@NativeClass(
    className = "SteamRemoteStorage",
    libraryName = "Steamworks",
    externalLinker = "de.potionlabs.SteamworksApi",
    platform = "Win64",
    isCClass = true)
interface SteamRemoteStorageSpec {
    @NativeFunction("SteamAPI_ISteamRemoteStorage_GetFileCount", true)
    fun getFileCount(): Int
    // int32 SteamAPI_ISteamRemoteStorage_GetFileCount( ISteamRemoteStorage* self );
}
