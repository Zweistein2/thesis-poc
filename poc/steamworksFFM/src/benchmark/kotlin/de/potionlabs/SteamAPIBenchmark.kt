package de.potionlabs

import de.potionlabs.specs.SteamAppsSpec
import de.potionlabs.specs.SteamFriendsSpec
import de.potionlabs.specs.SteamRemoteStorageSpec
import de.potionlabs.specs.SteamUGCSpec
import de.potionlabs.specs.SteamUserSpec
import de.potionlabs.specs.SteamUserStatsSpec
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
class SteamAPIBenchmark {
    val steamApi = SteamworksApi().getSteamApi()
    lateinit var friends: SteamFriendsSpec
    lateinit var ugc: SteamUGCSpec
    lateinit var remoteStorage: SteamRemoteStorageSpec
    lateinit var userStats: SteamUserStatsSpec
    lateinit var user: SteamUserSpec
    lateinit var apps: SteamAppsSpec

    @Setup
    fun init() {
        steamApi.initFlat()

        friends = steamApi.getSteamFriends()
        ugc = steamApi.getSteamUGC()
        remoteStorage = steamApi.getSteamRemoteStorage()
        userStats = steamApi.getSteamUserStats()
        user = steamApi.getSteamUser()
        apps = steamApi.getSteamApps()
    }

    @TearDown
    fun shutdown() {
        steamApi.shutdown()
    }

    @Benchmark
    fun benchmarkNumSubscribedItems() {
        ugc.getNumSubscribedItems()
    }

    @Benchmark
    fun benchmarkGetFileCount() {
        remoteStorage.getFileCount()
    }

    @Benchmark
    fun benchmarkGetNumAchievements() {
        userStats.getNumAchievements()
    }

    @Benchmark
    fun benchmarkIsBehindNAT() {
        user.isBehindNAT()
    }

    @Benchmark
    fun benchmarkGetPersonaName() {
        friends.getPersonaName()
    }
}