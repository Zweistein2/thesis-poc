package de.potionlabs

import com.codedisaster.steamworks.SteamLibraryLoader
import java.nio.file.Path
import java.nio.file.Paths

class SteamSharedLibraryLoader: SteamLibraryLoader {
    override fun loadLibrary(name: String): Boolean {
        try {
            val workingDir = Paths.get("").toAbsolutePath().toString()
            System.load(Path.of("$workingDir/src/main/resources/sdks/steamworks/redistributable_bin/win64/${name}64.dll").toAbsolutePath().toString())
            println("Library $name loaded successfully")
            return true
        } catch (e: UnsatisfiedLinkError) {
            println("Failed to load library $name: ${e.message}")
            return false
        }
    }
}