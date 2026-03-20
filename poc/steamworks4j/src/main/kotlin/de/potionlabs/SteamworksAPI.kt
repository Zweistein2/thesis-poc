package de.potionlabs

import com.codedisaster.steamworks.SteamAPI
import com.codedisaster.steamworks.SteamApps
import com.codedisaster.steamworks.SteamException
import com.codedisaster.steamworks.SteamFriends
import com.codedisaster.steamworks.SteamRemoteStorage
import com.codedisaster.steamworks.SteamUGC
import com.codedisaster.steamworks.SteamUser
import com.codedisaster.steamworks.SteamUserStats
import com.codedisaster.steamworks.SteamUtils
import de.potionlabs.callbacks.FriendsCallback
import de.potionlabs.callbacks.RemoteStorageCallback
import de.potionlabs.callbacks.UGCCallback
import de.potionlabs.callbacks.UserCallback
import de.potionlabs.callbacks.UserStatsCallback
import de.potionlabs.callbacks.UtilsCallback

class SteamworksAPI {
    lateinit var user: SteamUser
    lateinit var userStats: SteamUserStats
    lateinit var remoteStorage: SteamRemoteStorage
    lateinit var ugc: SteamUGC
    lateinit var utils: SteamUtils
    lateinit var apps: SteamApps
    lateinit var friends: SteamFriends

    fun init() {
        try {
            val loader = SteamSharedLibraryLoader()
            if (!SteamAPI.loadLibraries(loader)) {
                println("Failed to load native libraries")
            }

            if (!SteamAPI.init()) {
                println("Steamworks initialization error")
                SteamAPI.printDebugInfo(System.err)
            }
        } catch (e: SteamException) {
            println(e.message)
        }

        SteamAPI.printDebugInfo(System.out)
    }

    fun registerInterfaces(ugcCallback: UGCCallback = UGCCallback()) {
        user = SteamUser(UserCallback())
        userStats = SteamUserStats(UserStatsCallback())
        remoteStorage = SteamRemoteStorage(RemoteStorageCallback())
        ugc = SteamUGC(ugcCallback)
        utils = SteamUtils(UtilsCallback())
        apps = SteamApps()
        friends = SteamFriends(FriendsCallback())

        ugcCallback.setUgc(ugc)
    }

    fun unregisterInterfaces() {
        user.dispose()
        userStats.dispose()
        remoteStorage.dispose()
        ugc.dispose()
        utils.dispose()
        apps.dispose()
        friends.dispose()
    }

    fun runTicks() {
        if (SteamAPI.isSteamRunning()) {
            SteamAPI.runCallbacks();
        }
    }

    fun shutdown() {
        unregisterInterfaces()
        SteamAPI.shutdown()
    }
}