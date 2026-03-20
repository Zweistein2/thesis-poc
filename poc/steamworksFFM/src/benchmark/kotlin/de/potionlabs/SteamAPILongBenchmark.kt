package de.potionlabs

import de.potionlabs.enums.CallResultStructs
import de.potionlabs.enums.MatchingUGCType
import de.potionlabs.enums.UserUGCList
import de.potionlabs.enums.UserUGCListSortOrder
import de.potionlabs.specs.SteamAppsSpec
import de.potionlabs.specs.SteamFriendsSpec
import de.potionlabs.specs.SteamRemoteStorageSpec
import de.potionlabs.specs.SteamUGCSpec
import de.potionlabs.specs.SteamUserSpec
import de.potionlabs.specs.SteamUserStatsSpec
import de.potionlabs.specs.SteamUtilsSpec
import de.potionlabs.structs.UGCQueryCompleted
import de.potionlabs.structs.UGCQueryCompletedStruct
import de.potionlabs.tuples.GetAPICallResultReturnTuple
import de.potionlabs.tuples.GetItemInstallInfoReturnTuple
import de.potionlabs.tuples.GetQueryUGCResultReturnTuple
import de.potionlabs.tuples.IsAPICallCompletedReturnTuple
import de.potionlabs.tuples.ManualDispatchGetNextCallbackReturnTuple
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.benchmark.TearDown
import kotlinx.benchmark.Warmup

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 500, timeUnit = BenchmarkTimeUnit.MILLISECONDS)
@Measurement(iterations = 15, time = 500, timeUnit = BenchmarkTimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
class SteamAPILongBenchmark {
    val steamApi = SteamworksApi().getSteamApi()
    lateinit var friends: SteamFriendsSpec
    lateinit var ugc: SteamUGCSpec
    lateinit var remoteStorage: SteamRemoteStorageSpec
    lateinit var userStats: SteamUserStatsSpec
    lateinit var user: SteamUserSpec
    lateinit var apps: SteamAppsSpec
    lateinit var utils: SteamUtilsSpec

    @Setup
    fun init() {
        steamApi.initFlat()

        friends = steamApi.getSteamFriends()
        ugc = steamApi.getSteamUGC()
        remoteStorage = steamApi.getSteamRemoteStorage()
        userStats = steamApi.getSteamUserStats()
        user = steamApi.getSteamUser()
        apps = steamApi.getSteamApps()
        utils = steamApi.getSteamUtils()
    }

    @TearDown
    fun shutdown() {
        steamApi.shutdown()
    }

    @Benchmark
    fun benchmarkGetWorkshopItemInfo2(blackhole: Blackhole) {
        var handle = -1L
        val appId = steamApi.getSteamUtils().getAppID()
        val query = steamApi.getSteamUGC().createQueryUserUGCRequest(
            (steamApi.getSteamUser().getSteamID() % (1L shl 32)).toInt(), UserUGCList.SUBSCRIBED.code,
            MatchingUGCType.USABLE_IN_GAME.code, UserUGCListSortOrder.TITLE_ASC.code, appId, appId, 1)
        if(query != -1L) {
            handle = steamApi.getSteamUGC().sendQueryUGCRequest(query)
        }

        do {
            val apiCallCompleted = steamApi.getSteamUtils().isAPICallCompleted(handle) as IsAPICallCompletedReturnTuple
        } while(!apiCallCompleted.result)

        val apiCallResult = steamApi.getSteamUtils().getAPICallResult(
            handle,
            UGCQueryCompletedStruct.LAYOUT.byteSize().toInt(),
            CallResultStructs.UGC_QUERY_COMPLETED.id
        ) as GetAPICallResultReturnTuple

        val callback = apiCallResult.pCallback as UGCQueryCompleted
        for (i in 0 until callback.m_unNumResultsReturned) {
            val queryUGCResult = steamApi.getSteamUGC().getQueryUGCResult(callback.m_handle, i) as GetQueryUGCResultReturnTuple

            steamApi.getSteamUGC().downloadItem(queryUGCResult.pDetails.m_nPublishedFileId, true)
            val installInfo = steamApi.getSteamUGC().getItemInstallInfo(queryUGCResult.pDetails.m_nPublishedFileId, 1024) as GetItemInstallInfoReturnTuple
            blackhole.consume(installInfo)
        }

        steamApi.getSteamUGC().releaseQueryUGCRequest(handle)
    }
    
    @Benchmark
    fun benchmarkGetWorkshopItemInfo1(blackhole: Blackhole) {
        var handle = -1L
        val appId = steamApi.getSteamUtils().getAppID()
        val query = steamApi.getSteamUGC().createQueryUserUGCRequest(
            (steamApi.getSteamUser().getSteamID() % (1L shl 32)).toInt(), UserUGCList.SUBSCRIBED.code,
            MatchingUGCType.USABLE_IN_GAME.code, UserUGCListSortOrder.TITLE_ASC.code, appId, appId, 1)
        if(query != -1L) {
            handle = steamApi.getSteamUGC().sendQueryUGCRequest(query)
        }

        var tries = 0
        val hSteamPipe = steamApi.getHSteamPipe()

        while(tries < 30) {
            steamApi.manualDispatchRunFrame(hSteamPipe)

            val nextCallback = steamApi.manualDispatchGetNextCallback(hSteamPipe) as ManualDispatchGetNextCallbackReturnTuple

            if(nextCallback.invocationResult && nextCallback.pCallbackMsg.iCallback == CallResultStructs.UGC_QUERY_COMPLETED.id) {
                val ugcQueryCompleted = UGCQueryCompletedStruct(
                        nextCallback.pCallbackMsg.pubParam.reinterpret(
                        nextCallback.pCallbackMsg.cubParam.toLong()
                    )
                ).mapToKotlinStruct()

                for (i in 0 until ugcQueryCompleted.m_unNumResultsReturned) {
                    val queryUGCResult = steamApi.getSteamUGC().getQueryUGCResult(ugcQueryCompleted.m_handle, i) as GetQueryUGCResultReturnTuple

                    steamApi.getSteamUGC().downloadItem(queryUGCResult.pDetails.m_nPublishedFileId, true)
                    val installInfo = steamApi.getSteamUGC().getItemInstallInfo(queryUGCResult.pDetails.m_nPublishedFileId, 1024) as GetItemInstallInfoReturnTuple
                    blackhole.consume(installInfo)
                }

                steamApi.getSteamUGC().releaseQueryUGCRequest(handle)
                break
            } else {
                tries++
            }

            steamApi.manualDispatchFreeLastCallback(hSteamPipe)
        }
    }
}