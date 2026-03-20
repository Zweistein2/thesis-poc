package de.potionlabs

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
class SteamAPIBenchmark {
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
    fun benchmarkNumSubscribedItems() {
        steamApi.ugc.getNumSubscribedItems(true)
    }

    @Benchmark
    fun benchmarkGetFileCount() {
        steamApi.remoteStorage.fileCount
    }

    @Benchmark
    fun benchmarkGetNumAchievements() {
        steamApi.userStats.numAchievements
    }

    @Benchmark
    fun benchmarkIsBehindNAT() {
        steamApi.user.isBehindNAT
    }

    @Benchmark
    fun benchmarkGetPersonaName() {
        steamApi.friends.personaName
    }
}