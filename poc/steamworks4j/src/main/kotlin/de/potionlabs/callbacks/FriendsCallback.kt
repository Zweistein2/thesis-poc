package de.potionlabs.callbacks

import com.codedisaster.steamworks.SteamFriends
import com.codedisaster.steamworks.SteamFriendsCallback
import com.codedisaster.steamworks.SteamID

class FriendsCallback: SteamFriendsCallback {
    override fun onPersonaStateChange(steamID: SteamID?, change: SteamFriends.PersonaChange?) {
        when (change) {
            SteamFriends.PersonaChange.Name -> println("Persona name received: accountID=${steamID?.accountID}, name=${change.name}")
            else -> println("Persona state changed (unhandled): accountID=${steamID?.accountID}, change=${change?.name}")
        }
    }
}