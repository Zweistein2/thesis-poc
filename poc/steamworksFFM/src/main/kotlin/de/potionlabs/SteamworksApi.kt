package de.potionlabs

import de.potionlabs.specs.SteamAPISpec
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
        var loaderLookup: SymbolLookup = SymbolLookup.libraryLookup(Path.of("$workingDir/src/main/resources/sdks/steamworks/redistributable_bin/win64/steam_api64.dll"), Arena.global())
    }

    fun getSteamApi(path: String): SteamAPISpec {
        loaderLookup = SymbolLookup.libraryLookup(Path.of(path), Arena.global())

        return getSteamApi()
    }

    fun getSteamApi(): SteamAPISpec {
        try {
            val implClass = Class.forName("de.potionlabs.specs.SteamAPI")
            return implClass.getDeclaredConstructor().newInstance() as SteamAPISpec
        } catch (e: Exception) {
            throw RuntimeException("No Impl found for SteamAPISpec", e)
        }
    }
}