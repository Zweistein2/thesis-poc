package de.potionlabs.structs

import de.potionlabs.ffmlibrary.annotations.NativeStruct
import de.potionlabs.ffmlibrary.utils.Struct

@NativeStruct("Steamworks", 4700)
data class InventoryResultReady(
    val m_handle: Int,
    val m_result: Int
) : Struct