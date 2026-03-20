package de.potionlabs

import com.codedisaster.steamworks.SteamUGC
import de.potionlabs.callbacks.UGCCallback
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
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
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
class SteamAPILongBenchmark {
    val steamApi = SteamworksAPI()
    val ugcCallback = UGCCallback()

    @Setup
    fun init() {
        steamApi.init()
        steamApi.registerInterfaces(ugcCallback)
    }

    @TearDown
    fun shutdown() {
        steamApi.shutdown()
    }

    @Benchmark
    fun benchmarkGetWorkshopItemInfo1() {
        val appId = steamApi.utils.appID
        val query = steamApi.ugc.createQueryUserUGCRequest(
            steamApi.user.steamID.accountID, SteamUGC.UserUGCList.Subscribed,
            SteamUGC.MatchingUGCType.UsableInGame, SteamUGC.UserUGCListSortOrder.TitleAsc, appId, appId, 1)
        if(query.isValid) {
            steamApi.ugc.sendQueryUGCRequest(query)
            ugcCallback.setWaitingForCallback(true)
        }

        while(ugcCallback.isWaitingForCallback()) {
            steamApi.runTicks()
        }
    }
}