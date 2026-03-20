package de.potionlabs.structs

import de.potionlabs.ffmlibrary.annotations.NativeStruct
import de.potionlabs.ffmlibrary.utils.Struct
import java.lang.foreign.MemorySegment

@NativeStruct("Steamworks")
data class CallbackMsg(
    val hSteamUser: Int,
    val iCallback: Int,
    val pubParam: MemorySegment,
    val cubParam: Int
) : Struct