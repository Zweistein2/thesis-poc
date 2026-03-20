package de.potionlabs.callbacks

import com.codedisaster.steamworks.SteamUtilsCallback

class UtilsCallback: SteamUtilsCallback {
    override fun onSteamShutdown() {
        println("Steam is shutting down")
    }
}