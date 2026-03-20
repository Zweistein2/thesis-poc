package de.potionlabs

import com.codedisaster.steamworks.SteamUGC
import de.potionlabs.callbacks.UGCCallback
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class SteamworksAPITest {
    companion object {
        val steamApi = SteamworksAPI()
        val ugcCallback = UGCCallback()

        @JvmStatic
        @BeforeAll
        fun init() {
            println("\n----- BEGINNING TESTS -----\n")
            steamApi.init()
            steamApi.registerInterfaces(ugcCallback)
        }

        @JvmStatic
        @AfterAll
        fun shutdown() {
            println("\n----- ENDING TESTS -----\n")
            steamApi.shutdown()
        }
    }

    @Test
    fun testGetWorkshopItemInfo() {
        println("----- Testing: Get infos about workshop items -----")
        val appId = steamApi.utils.appID
        val query = steamApi.ugc.createQueryUserUGCRequest(steamApi.user.steamID.accountID, SteamUGC.UserUGCList.Subscribed,
            SteamUGC.MatchingUGCType.UsableInGame, SteamUGC.UserUGCListSortOrder.TitleAsc, appId, appId, 1)
        if(query.isValid) {
            println("UGC-Query is valid and will be sent: $query")
            steamApi.ugc.sendQueryUGCRequest(query)
            ugcCallback.setWaitingForCallback(true)
        }

        while(ugcCallback.isWaitingForCallback()) {
            Thread.sleep(50) // ~20 Times per Second
            steamApi.runTicks()
        }

        assert(true)
    }

    @Test
    fun testNumSubscribedItems() {
        println("----- Testing: getNumSubscribedItems-Method -----")
        val numSubscribedItems = steamApi.ugc.getNumSubscribedItems(true)

        println("numSubscribedItems: $numSubscribedItems")

        assertTrue { numSubscribedItems >= 0 }
    }

    @Test
    fun testGetFileCount() {
        println("----- Testing: getFileCount-Method -----")
        val fileCount = steamApi.remoteStorage.fileCount

        println("fileCount: $fileCount")

        assertTrue { fileCount >= 0 }
    }

    @Test
    fun testGetNumAchievements() {
        println("----- Testing: getNumAchievements-Method -----")
        val numAchievements = steamApi.userStats.numAchievements

        println("numAchievements: $numAchievements")

        assertTrue { numAchievements >= 0 }
    }

    @Test
    fun testIsSubscribedApp() {
        println("----- Testing: isSubscribedApp-Method -----")
        val isAppInstalled = steamApi.apps.isSubscribedApp(steamApi.utils.appID)

        println("isAppInstalled: $isAppInstalled")

        assert(isAppInstalled)
    }

    @Test
    fun testIsBehindNAT() {
        println("----- Testing: isBehindNAT-Method -----")
        val isBehindNAT = steamApi.user.isBehindNAT

        println("isBehindNAT: $isBehindNAT")

        assert(isBehindNAT)
    }

    @Test
    fun testGetPersonaName() {
        println("----- Testing: getPersonaName-Method -----")
        val getPersonaName = steamApi.friends.personaName

        println("getPersonaName: $getPersonaName")

        assert(getPersonaName.isNotEmpty())
    }
}