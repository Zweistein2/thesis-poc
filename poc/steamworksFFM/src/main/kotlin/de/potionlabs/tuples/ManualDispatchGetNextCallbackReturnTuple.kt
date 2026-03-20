package de.potionlabs.tuples

import de.potionlabs.structs.CallbackMsg
import de.potionlabs.ffmlibrary.annotations.NativeTuple
import de.potionlabs.ffmlibrary.utils.ReturnTuple

@NativeTuple(
    libraryName = "Steamworks",
    className = "SteamAPI"
)
data class ManualDispatchGetNextCallbackReturnTuple(
    val invocationResult: Boolean,
    val pCallbackMsg: CallbackMsg
) : ReturnTuple
