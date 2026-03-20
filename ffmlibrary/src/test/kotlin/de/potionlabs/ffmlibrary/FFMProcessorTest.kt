package de.potionlabs.ffmlibrary

import com.tschuchort.compiletesting.*
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode

import org.hamcrest.MatcherAssert.assertThat
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.descriptors.runtime.components.tryLoadClass
import org.junit.jupiter.api.Test
import java.nio.file.Paths
import kotlin.io.path.listDirectoryEntries
import kotlin.test.assertNotNull

@OptIn(ExperimentalCompilerApi::class)
class FFMProcessorTest {
    @Test
    fun kspTest() {
        val path = Paths.get("").toAbsolutePath().resolve("src/main/kotlin/de/potionlabs/ffmlibrary")
        val annotations =
            path.resolve("annotations").listDirectoryEntries()
                .map {
                    SourceFile.new(it.toFile().name, it.toFile().readText())
                }.toMutableList()
        val utils =
            path.resolve("utils").listDirectoryEntries()
                .map {
                    SourceFile.new(it.toFile().name, it.toFile().readText())
                }.toMutableList()

        val steamApiSpec = SourceFile.kotlin(
            "SteamAPISpec.kt", """
                package de.potionlabs.ffmlibrary.specs
                
                import de.potionlabs.ffmlibrary.annotations.NativeClass
                import de.potionlabs.ffmlibrary.annotations.NativeFunction
                import de.potionlabs.ffmlibrary.annotations.NativeFunctionParam
                import de.potionlabs.ffmlibrary.annotations.NativeFunctionPointerStringParam
                import de.potionlabs.ffmlibrary.annotations.NativeFunctionReturnType
                import de.potionlabs.ffmlibrary.utils.ReturnTuple
                import de.potionlabs.ffmlibrary.utils.Datatype
                
                @NativeClass(
                    className = "SteamAPI",
                    libraryName = "Steamworks",
                    externalLinker = "de.potionlabs.ffmlibrary.SteamworksApi",
                    platform = "Win64")
                interface SteamAPISpec {
                    @NativeFunction("SteamAPI_InitFlat")
                    @NativeFunctionReturnType(Datatype.INTEGER)
                    @NativeFunctionPointerStringParam("pOutErrMsg", Datatype.STRING, 0, 1024)
                    fun initFlat(): ReturnTuple
                    
                    @NativeFunction("SteamAPI_RunCallbacks")
                    fun runCallbacks()
                    
                    @NativeFunction("SteamAPI_RestartAppIfNecessary")
                    fun restartAppIfNecessary(@NativeFunctionParam(0) appId: Int): Boolean
                    
                    @NativeFunction("SteamAPI_SteamInventory_v003")
                    fun getSteamInventory(): SteamInventorySpec
                    
                    @NativeFunction("SteamAPI_SteamApps_v008")
                    fun getSteamApps(): SteamAppsSpec
                }
            """.trimIndent()
        )
        val steamInventorySpec = SourceFile.kotlin(
            "SteamInventorySpec.kt", """
                package de.potionlabs.ffmlibrary.specs
                
                import de.potionlabs.ffmlibrary.annotations.NativeClass
                import de.potionlabs.ffmlibrary.annotations.NativeFunction
                import de.potionlabs.ffmlibrary.annotations.NativeFunctionParam
                import de.potionlabs.ffmlibrary.annotations.NativeFunctionReturnType
                import de.potionlabs.ffmlibrary.annotations.NativeFunctionPointerParam
                import de.potionlabs.ffmlibrary.annotations.NativeFunctionPointerStructParam
                import de.potionlabs.ffmlibrary.utils.Datatype
                import de.potionlabs.ffmlibrary.utils.SteamItemDetails
                import de.potionlabs.ffmlibrary.utils.ReturnTuple
                
                @NativeClass(
                    className = "SteamInventory",
                    libraryName = "Steamworks",
                    externalLinker = "de.potionlabs.ffmlibrary.SteamworksApi",
                    platform = "Win64",
                    isCClass = true)
                interface SteamInventorySpec {
                    @NativeFunction("SteamAPI_ISteamInventory_GetAllItems", true)
                    @NativeFunctionReturnType(Datatype.BOOLEAN)
                    @NativeFunctionPointerParam("pResultHandle", Datatype.INTEGER, 1, "")
                    fun getAllItems(): ReturnTuple
                    
                    @NativeFunction("SteamAPI_ISteamInventory_GetResultItems", true)
                    @NativeFunctionReturnType(Datatype.BOOLEAN)
                    @NativeFunctionPointerStructParam("pOutItemsArray", SteamItemDetails::class, 1, true, "-1")
                    @NativeFunctionPointerParam("punOutItemsArraySize", Datatype.INTEGER, 2, "")
                    fun getResultItemsSize(@NativeFunctionParam(0) pResultHandle: Int): ReturnTuple
                    
                    @NativeFunction("SteamAPI_ISteamInventory_GetResultItems", true)
                    @NativeFunctionReturnType(Datatype.BOOLEAN)
                    @NativeFunctionPointerStructParam("pOutItemsArray", SteamItemDetails::class, 1, true, "itemArraySize")
                    @NativeFunctionPointerParam("punOutItemsArraySize", Datatype.INTEGER, 2, "itemArraySize")
                    fun getResultItems(@NativeFunctionParam(0) pResultHandle: Int, @NativeFunctionParam(-1) itemArraySize: Int): ReturnTuple
                    
                    @NativeFunction("SteamAPI_ISteamInventory_GetResultStatus", true)
                    fun getResultStatus(@NativeFunctionParam(0) pResultHandle: Int): Int
                }
            """.trimIndent()
        )
        val steamAppsSpec = SourceFile.kotlin(
            "SteamAppsSpec.kt", """
                package de.potionlabs.ffmlibrary.specs
                
                import de.potionlabs.ffmlibrary.annotations.NativeClass
                import de.potionlabs.ffmlibrary.annotations.NativeFunction
                import de.potionlabs.ffmlibrary.annotations.NativeFunctionParam
                import de.potionlabs.ffmlibrary.annotations.NativeFunctionReturnType
                import de.potionlabs.ffmlibrary.annotations.NativeFunctionPointerParam
                import de.potionlabs.ffmlibrary.annotations.NativeFunctionPointerStructParam
                import de.potionlabs.ffmlibrary.utils.SteamItemDetails
                import de.potionlabs.ffmlibrary.utils.ReturnTuple
                
                @NativeClass(
                    className = "SteamApps",
                    libraryName = "Steamworks",
                    externalLinker = "de.potionlabs.ffmlibrary.SteamworksApi",
                    platform = "Win64",
                    isCClass = true)
                interface SteamAppsSpec {
                    @NativeFunction("SteamAPI_ISteamApps_GetCurrentGameLanguage", true)
                    fun getCurrentGameLanguage(): String
                
                    @NativeFunction("SteamAPI_ISteamApps_GetAvailableGameLanguages", true)
                    fun getAvailableGameLanguages(): String
                }
            """.trimIndent()
        )
        val steamItemDetails = SourceFile.kotlin(
            "SteamItemDetails.kt", """
                package de.potionlabs.ffmlibrary.utils
                
                import de.potionlabs.ffmlibrary.annotations.NativeStruct
                
                @NativeStruct(libraryName = "Steamworks")
                data class SteamItemDetails(
                    val itemId: Long,
                    val definitionId: Int,
                    val quantity: Short,
                    val flags: Short
                ): Struct
            """.trimIndent()
        )
        val getAllItemsReturnTupleFile = SourceFile.kotlin(
            "GetAllItemsReturnTuple.kt", """
                package de.potionlabs.ffmlibrary.utils
                
                import de.potionlabs.ffmlibrary.annotations.NativeTuple
                
                @NativeTuple(
                    libraryName = "Steamworks",
                    className = "SteamInventory"
                )
                data class GetAllItemsReturnTuple(
                    val invocationResult: Boolean,
                    val pResultHandleValue: Int,
                ) : ReturnTuple
            """.trimIndent()
        )
        val getResultItemsSizeReturnTupleFile = SourceFile.kotlin(
            "GetResultItemsSizeReturnTuple.kt", """
                package de.potionlabs.ffmlibrary.utils
                
                import de.potionlabs.ffmlibrary.annotations.NativeTuple
                
                @NativeTuple(
                    libraryName = "Steamworks",
                    className = "SteamInventory"
                )
                data class GetResultItemsSizeReturnTuple(
                    val invocationResult: Boolean,
                    val punOutItemsArraySizeValue: Int,
                    val pOutItemsArrayValue: List<SteamItemDetails>,
                ) : ReturnTuple
            """.trimIndent()
        )
        val getResultItemsReturnTupleFile = SourceFile.kotlin(
            "GetResultItemsReturnTuple.kt", """
                package de.potionlabs.ffmlibrary.utils
                
                import de.potionlabs.ffmlibrary.annotations.NativeTuple
                
                @NativeTuple(
                    libraryName = "Steamworks",
                    className = "SteamInventory"
                )
                data class GetResultItemsReturnTuple(
                    val invocationResult: Boolean,
                    val punOutItemsArraySizeValue: Int,
                    val pOutItemsArrayValue: List<SteamItemDetails>,
                ) : ReturnTuple
            """.trimIndent()
        )
        val initFlatReturnTupleFile = SourceFile.kotlin(
            "InitFlatReturnTuple.kt", """
                package de.potionlabs.ffmlibrary.utils
                
                import de.potionlabs.ffmlibrary.annotations.NativeTuple
                
                @NativeTuple(
                    libraryName = "Steamworks",
                    className = "SteamAPI"
                )
                data class InitFlatReturnTuple(
                    val invocationResult: Int,
                    val pOutErrMsgValue: String,
                ) : ReturnTuple
            """.trimIndent()
        )
        val apiFile = SourceFile.kotlin(
            "SteamworksApi.kt", """
                package de.potionlabs.ffmlibrary

                import java.lang.foreign.Linker
                import java.lang.foreign.Arena
                import java.lang.foreign.SymbolLookup
                import java.nio.file.Path
                import java.nio.file.Paths

                class SteamworksApi {
                    companion object {
                        private var workingDir = Paths.get("").toAbsolutePath().toString()
                
                        val nativeLinker: Linker = Linker.nativeLinker()
                        val stdLibLookup: SymbolLookup = nativeLinker.defaultLookup()
                        var loaderLookup: SymbolLookup = SymbolLookup.libraryLookup(Path.of("${'$'}workingDir/src/test/resources/sdks/steamworks/redistributable_bin/win64/steam_api64.dll"), Arena.global())
                    }
                }
            """.trimIndent()
        )

        val sourceFiles = mutableListOf(steamApiSpec, steamInventorySpec, steamAppsSpec, steamItemDetails, getAllItemsReturnTupleFile, getResultItemsSizeReturnTupleFile, getResultItemsReturnTupleFile, initFlatReturnTupleFile)
        sourceFiles.addAll(annotations)
        sourceFiles.addAll(utils)
        sourceFiles.add(apiFile)

        val compilation = KotlinCompilation().apply {
            sources = sourceFiles
            kspWithCompilation = true
            configureKsp(useKsp2 = true) {
                loggingLevels = CompilerMessageSeverity.entries.toSet()
                symbolProcessorProviders.add(FFMProcessorProvider())
            }
        }

        val result = compilation.compile()

        println("ExitCode: " + result.exitCode)
        println("Message: " + result.messages)
        assertThat("Compilation has to be successful", result.exitCode == ExitCode.OK)
        assertThat("Files have to be generated", result.generatedFiles.isNotEmpty())

        val steamInventory = result.classLoader.tryLoadClass("de.potionlabs.ffmlibrary.specs.SteamInventory")
        val steamApps = result.classLoader.tryLoadClass("de.potionlabs.ffmlibrary.specs.SteamApps")
        val steamAPI = result.classLoader.tryLoadClass("de.potionlabs.ffmlibrary.specs.SteamAPI")
        val getAllItemsReturnTuple = result.classLoader.tryLoadClass("de.potionlabs.ffmlibrary.utils.GetAllItemsReturnTuple")
        val getResultItemsReturnTuple = result.classLoader.tryLoadClass("de.potionlabs.ffmlibrary.utils.GetResultItemsReturnTuple")
        val getResultItemsSizeReturnTuple = result.classLoader.tryLoadClass("de.potionlabs.ffmlibrary.utils.GetResultItemsSizeReturnTuple")
        val initFlatReturnTuple = result.classLoader.tryLoadClass("de.potionlabs.ffmlibrary.utils.InitFlatReturnTuple")

        assertNotNull(steamInventory)
        assertNotNull(steamApps)
        assertNotNull(steamAPI)
        assertNotNull(getAllItemsReturnTuple)
        assertNotNull(getResultItemsReturnTuple)
        assertNotNull(getResultItemsSizeReturnTuple)
        assertNotNull(initFlatReturnTuple)

        assertThat(
            "SteamInventory-Class has to be generated",
            steamInventory.methods.isNotEmpty()
        )
        assertThat(
            "SteamAPI-Class has to be generated",
            steamAPI.methods.isNotEmpty()
        )

        val generatedImpls = result.outputDirectory.parentFile.walkTopDown()
            .filter { it.isFile && it.extension == "kt" && !it.nameWithoutExtension.endsWith("Spec") }.toList()
        generatedImpls.forEach {
            println(it.readText())
            println("--- ---\n")
        }
    }
}