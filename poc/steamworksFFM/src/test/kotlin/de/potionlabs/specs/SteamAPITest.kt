package de.potionlabs.specs

import de.potionlabs.SteamworksApi
import de.potionlabs.enums.CallResultStructs
import de.potionlabs.tuples.GetAllItemsReturnTuple
import de.potionlabs.enums.InitResult
import de.potionlabs.enums.MatchingUGCType
import de.potionlabs.enums.Result
import de.potionlabs.enums.UserUGCList
import de.potionlabs.enums.UserUGCListSortOrder
import de.potionlabs.structs.InventoryResultReadyStruct
import de.potionlabs.structs.UGCQueryCompleted
import de.potionlabs.structs.UGCQueryCompletedStruct
import de.potionlabs.tuples.GetAPICallResultReturnTuple
import de.potionlabs.tuples.GetItemInstallInfoReturnTuple
import de.potionlabs.tuples.GetQueryUGCResultReturnTuple
import de.potionlabs.tuples.GetResultItemsReturnTuple
import de.potionlabs.tuples.GetResultItemsSizeReturnTuple
import de.potionlabs.tuples.InitFlatReturnTuple
import de.potionlabs.tuples.IsAPICallCompletedReturnTuple
import de.potionlabs.tuples.ManualDispatchGetNextCallbackReturnTuple
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class SteamAPITest {
    companion object {
        val steamApi = SteamworksApi().getSteamApi()

        @JvmStatic
        @BeforeAll
        fun init() {
            //println("restart: ${steamApi.restartAppIfNecessary(688610)}")
            val result = steamApi.initFlat() as InitFlatReturnTuple
            val resultCode = InitResult.getByCode(result.invocationResult)
            val errorMsg = result.pOutErrMsgValue
            println("init: $resultCode")
            println("error: $errorMsg")
            assert(resultCode == InitResult.OK)
            assert(errorMsg.isBlank())
            steamApi.manualDispatchInit()
            println("\n----- BEGINNING TESTS -----\n")
        }

        @JvmStatic
        @AfterAll
        fun shutdown() {
            println("\n----- ENDING TESTS -----\n")
            println("shutdown")
            steamApi.shutdown()
        }
    }
    
    @Test
    fun testGetWorkshopItemInfo() {
        println("----- Testing: Get infos about workshop items -----")

        var handle = -1L
        val appId = steamApi.getSteamUtils().getAppID()
        val query = steamApi.getSteamUGC().createQueryUserUGCRequest(
            (steamApi.getSteamUser().getSteamID() % (1L shl 32)).toInt(), UserUGCList.SUBSCRIBED.code,
            MatchingUGCType.USABLE_IN_GAME.code, UserUGCListSortOrder.TITLE_ASC.code, appId, appId, 1)
        if(query != -1L) {
            println("UGC-Query is valid and will be sent: $query")
            handle = steamApi.getSteamUGC().sendQueryUGCRequest(query)
        }

        do {
            val apiCallCompleted = steamApi.getSteamUtils().isAPICallCompleted(handle) as IsAPICallCompletedReturnTuple
        } while(!apiCallCompleted.result)

        val apiCallResult = steamApi.getSteamUtils().getAPICallResult(
            handle,
            UGCQueryCompletedStruct.LAYOUT.byteSize().toInt(),
            CallResultStructs.UGC_QUERY_COMPLETED.id,
        ) as GetAPICallResultReturnTuple
        println("API call result: ${apiCallResult.result}")
        println("API call failed: ${apiCallResult.pbFailed}")
        val callback = apiCallResult.pCallback as UGCQueryCompleted
        println("Result: ${callback.m_eResult}")
        println("NextCursor: ${callback.m_rgchNextCursor}")
        println("Returned ${callback.m_unNumResultsReturned} of ${callback.m_unTotalMatchingResults} Results")

        for (i in 0 until callback.m_unNumResultsReturned) {
            val queryUGCResult = steamApi.getSteamUGC().getQueryUGCResult(callback.m_handle, i) as GetQueryUGCResultReturnTuple

            steamApi.getSteamUGC().downloadItem(queryUGCResult.pDetails.m_nPublishedFileId, true)
            val installInfo = steamApi.getSteamUGC().getItemInstallInfo(queryUGCResult.pDetails.m_nPublishedFileId, 1024) as GetItemInstallInfoReturnTuple

            println("folder: ${installInfo.pchFolder}")
            println("sizeOnDisk: ${installInfo.punSizeOnDisk}")
        }

        val released = steamApi.getSteamUGC().releaseQueryUGCRequest(handle)
        println("Released query with handle $handle: $released")
    }
    
    @Test
    fun testIsSubscribed() {
        println("----- Testing: isSubscribed-Method -----")
        val isSubscribed = steamApi.getSteamApps().isSubscribed()

        println("isSubscribed: $isSubscribed")

        assert(isSubscribed)
    }

    @Test
    fun testIsVACBanned() {
        println("----- Testing: isVACBanned-Method -----")
        val isVACBanned = steamApi.getSteamApps().isVACBanned()

        println("isVACBanned: $isVACBanned")

        assertFalse(isVACBanned)
    }

    @Test
    fun testIsAppInstalled() {
        println("----- Testing: isAppInstalled-Method -----")
        val isAppInstalled = steamApi.getSteamApps().isAppInstalled(steamApi.getSteamUtils().getAppID())

        println("isAppInstalled: $isAppInstalled")

        assert(isAppInstalled)
    }

    @Test
    fun testGetDLCCount() {
        println("----- Testing: getDLCCount-Method -----")
        val dlcCount = steamApi.getSteamApps().getDLCCount()

        println("getDLCCount: $dlcCount")

        assert(dlcCount > -1)
    }

    @Test
    fun testGetAvailableGameLanguages() {
        println("----- Testing: getAvailableGameLanguages-Method -----")
        val availableGameLanguages = steamApi.getSteamApps().getAvailableGameLanguages()

        println("getAvailableGameLanguages: $availableGameLanguages")

        assert(availableGameLanguages.isNotBlank())
    }

    @Test
    fun testGetCurrentGameLanguage() {
        println("----- Testing: getCurrentGameLanguage-Method -----")
        val currentGameLanguage = steamApi.getSteamApps().getCurrentGameLanguage()

        println("getCurrentGameLanguage: $currentGameLanguage")

        assert(currentGameLanguage.isNotBlank())
    }

    @Test
    fun testGetAppBuildId() {
        println("----- Testing: getAppBuildId-Method -----")
        val appBuildId = steamApi.getSteamApps().getAppBuildId()

        println("getAppBuildId: $appBuildId")

        assert(true)
    }

    @Test
    fun testLoggedOn() {
        println("----- Testing: loggedOn-Method -----")
        val loggedOn = steamApi.getSteamUser().loggedOn()

        println("loggedOn: $loggedOn")

        assert(loggedOn)
    }

    @Test
    fun testIsBehindNAT() {
        println("----- Testing: isBehindNAT-Method -----")
        val isBehindNAT = steamApi.getSteamUser().isBehindNAT()

        println("isBehindNAT: $isBehindNAT")

        assert(isBehindNAT)
    }

    @Test
    fun testIsTwoFactorEnabled() {
        println("----- Testing: isTwoFactorEnabled-Method -----")
        val isTwoFactorEnabled = steamApi.getSteamUser().isTwoFactorEnabled()

        println("isTwoFactorEnabled: $isTwoFactorEnabled")

        assert(isTwoFactorEnabled)
    }

    @Test
    fun testIsPhoneIdentifying() {
        println("----- Testing: isPhoneIdentifying-Method -----")
        val isPhoneIdentifying = steamApi.getSteamUser().isPhoneIdentifying()

        println("isPhoneIdentifying: $isPhoneIdentifying")

        assertFalse(isPhoneIdentifying)
    }

    @Test
    fun testIsPhoneVerified() {
        println("----- Testing: isPhoneVerified-Method -----")
        val isPhoneVerified = steamApi.getSteamUser().isPhoneVerified()

        println("isPhoneVerified: $isPhoneVerified")

        assert(isPhoneVerified)
    }

    @Test
    fun testIsPhoneRequiringVerification() {
        println("----- Testing: isPhoneRequiringVerification-Method -----")
        val isPhoneRequiringVerification = steamApi.getSteamUser().isPhoneRequiringVerification()

        println("isPhoneRequiringVerification: $isPhoneRequiringVerification")

        assertFalse(isPhoneRequiringVerification)
    }

    @Test
    fun testGetPersonaName() {
        println("----- Testing: getPersonaName-Method -----")
        val getPersonaName = steamApi.getSteamFriends().getPersonaName()

        println("getPersonaName: $getPersonaName")

        assert(getPersonaName.isNotEmpty())
    }

    @Test
    fun testGetAllItems() {
        println("----- Testing: getAllItems-Method -----")
        val getAllItemsReturnTuple = steamApi.getSteamInventory().getAllItems() as GetAllItemsReturnTuple
        val result = getAllItemsReturnTuple.invocationResult
        println("result: $result")

        assert(result)

        val itemHandle = getAllItemsReturnTuple.pResultHandleValue
        println("itemHandle: $itemHandle")

        assert(itemHandle > 0)
    }

    @Test
    fun testGetResultStatus() {
        println("----- Testing: getResultStatus-Method -----")
        val steamInventory = steamApi.getSteamInventory()
        val getAllItemsReturnTuple = steamInventory.getAllItems() as GetAllItemsReturnTuple

        val itemHandle = getAllItemsReturnTuple.pResultHandleValue
        val status = steamInventory.getResultStatus(itemHandle)

        println("status: $status")
        assertNotEquals(Result.FAIL.code, status)

        var tries = 0
        val hSteamPipe = steamApi.getHSteamPipe()

        while(tries < 30) {
            steamApi.manualDispatchRunFrame(hSteamPipe)

            val nextCallback = steamApi.manualDispatchGetNextCallback(hSteamPipe) as ManualDispatchGetNextCallbackReturnTuple

            if(nextCallback.invocationResult && nextCallback.pCallbackMsg.iCallback == CallResultStructs.INVENTORY_RESULT_READY.id) {
                println("Received callback with id: ${nextCallback.pCallbackMsg.iCallback}")
                println("cubParam: ${nextCallback.pCallbackMsg.cubParam}")
                println("pubParam: ${nextCallback.pCallbackMsg.pubParam}")
                println("hSteamUser: ${nextCallback.pCallbackMsg.hSteamUser}")

                val inventoryResultReady = InventoryResultReadyStruct(
                    nextCallback.pCallbackMsg.pubParam.reinterpret(
                        nextCallback.pCallbackMsg.cubParam.toLong()
                    )
                ).mapToKotlinStruct()

                println("result: ${inventoryResultReady.m_result}")

                assertEquals(Result.OK.code, inventoryResultReady.m_result)

                break
            } else {
                Thread.sleep(50)
                tries++
            }

            steamApi.manualDispatchFreeLastCallback(hSteamPipe)
        }

        assert(tries < 30)
    }

    @Test
    fun testGetResultItems() {
        println("----- Testing: getResultItems-Method -----")
        val steamInventory = steamApi.getSteamInventory()

        var tries = 0
        val hSteamPipe = steamApi.getHSteamPipe()

        while(tries < 30) {
            steamApi.manualDispatchRunFrame(hSteamPipe)

            val nextCallback = steamApi.manualDispatchGetNextCallback(hSteamPipe) as ManualDispatchGetNextCallbackReturnTuple
            if(nextCallback.invocationResult && nextCallback.pCallbackMsg.iCallback == CallResultStructs.INVENTORY_RESULT_READY.id) {
                println("Received callback with id: ${nextCallback.pCallbackMsg.iCallback}")

                val inventoryResultReady = InventoryResultReadyStruct(
                    nextCallback.pCallbackMsg.pubParam.reinterpret(
                        nextCallback.pCallbackMsg.cubParam.toLong()
                    )
                ).mapToKotlinStruct()

                assertEquals(Result.OK.code, inventoryResultReady.m_result)

                val getResultItemsSizeReturnTuple = steamInventory.getResultItemsSize(inventoryResultReady.m_handle) as GetResultItemsSizeReturnTuple
                var result = getResultItemsSizeReturnTuple.invocationResult
                println("result: $result")

                assert(result)

                val itemCount = getResultItemsSizeReturnTuple.punOutItemsArraySizeValue
                println("itemCount: $itemCount")

                assert(itemCount > 0)

                val getResultItemsReturnTuple = steamInventory.getResultItems(inventoryResultReady.m_handle, itemCount) as GetResultItemsReturnTuple
                result = getResultItemsReturnTuple.invocationResult
                println("result: $result")

                assert(result)

                val content = getResultItemsReturnTuple.pOutItemsArrayValue
                println("content: ${content.joinToString("\n")}")

                assert(content.isNotEmpty())
                assert(content.first().itemId > -1)
                break
            } else {
                Thread.sleep(50)
                tries++
            }

            steamApi.manualDispatchFreeLastCallback(hSteamPipe)
        }

        assert(tries < 30)
    }
}