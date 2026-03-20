package de.potionlabs.specs

import de.potionlabs.structs.SteamItemDetails
import de.potionlabs.ffmlibrary.annotations.*
import de.potionlabs.ffmlibrary.utils.Datatype
import de.potionlabs.ffmlibrary.utils.ReturnTuple

@NativeClass(
    className = "SteamInventory",
    libraryName = "Steamworks",
    externalLinker = "de.potionlabs.SteamworksApi",
    platform = "Win64",
    isCClass = true
)
interface SteamInventorySpec {
    @NativeFunction("SteamAPI_ISteamInventory_GetAllItems", true)
    @NativeFunctionReturnType(Datatype.BOOLEAN)
    @NativeFunctionPointerParam("pResultHandle", Datatype.INTEGER, 1, "")
    fun getAllItems(): ReturnTuple
    // bool SteamAPI_ISteamInventory_GetAllItems( ISteamInventory* self, SteamInventoryResult_t * pResultHandle );

    @NativeFunction("SteamAPI_ISteamInventory_GetResultItems", true)
    @NativeFunctionReturnType(Datatype.BOOLEAN)
    @NativeFunctionPointerStructParam("pOutItemsArray", SteamItemDetails::class, 1, true, "-1")
    @NativeFunctionPointerParam("punOutItemsArraySize", Datatype.INTEGER, 2, "")
    fun getResultItemsSize(
        @NativeFunctionParam(0) pResultHandle: Int
    ): ReturnTuple
    // bool SteamAPI_ISteamInventory_GetResultItems( ISteamInventory* self, SteamInventoryResult_t resultHandle, SteamItemDetails_t * pOutItemsArray, uint32 * punOutItemsArraySize );

    @NativeFunction("SteamAPI_ISteamInventory_GetResultItems", true)
    @NativeFunctionReturnType(Datatype.BOOLEAN)
    @NativeFunctionPointerStructParam("pOutItemsArray", SteamItemDetails::class, 1, true, "itemArraySize")
    @NativeFunctionPointerParam("punOutItemsArraySize", Datatype.INTEGER, 2, "itemArraySize")
    fun getResultItems(
        @NativeFunctionParam(0) pResultHandle: Int,
        @NativeFunctionParam(-1) itemArraySize: Int
    ): ReturnTuple
    // bool SteamAPI_ISteamInventory_GetResultItems( ISteamInventory* self, SteamInventoryResult_t resultHandle, SteamItemDetails_t * pOutItemsArray, uint32 * punOutItemsArraySize );

    @NativeFunction("SteamAPI_ISteamInventory_GetResultStatus", true)
    fun getResultStatus(
        @NativeFunctionParam(0) pResultHandle: Int
    ): Int
    // EResult SteamAPI_ISteamInventory_GetResultStatus( ISteamInventory* self, SteamInventoryResult_t resultHandle );
}
