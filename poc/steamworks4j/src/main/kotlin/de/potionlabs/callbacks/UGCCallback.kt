package de.potionlabs.callbacks

import com.codedisaster.steamworks.SteamPublishedFileID
import com.codedisaster.steamworks.SteamResult
import com.codedisaster.steamworks.SteamUGC
import com.codedisaster.steamworks.SteamUGCCallback
import com.codedisaster.steamworks.SteamUGCDetails
import com.codedisaster.steamworks.SteamUGCQuery

class UGCCallback: SteamUGCCallback {
    private lateinit var ugc: SteamUGC
    private var waitingForCallback = false

    override fun onUGCQueryCompleted(
        query: SteamUGCQuery?,
        numResultsReturned: Int,
        totalMatchingResults: Int,
        isCachedData: Boolean,
        result: SteamResult?
    ) {
        // println("UGC Query completed: handle=${query.toString()} $numResultsReturned of $totalMatchingResults results returned, result=${result.toString()}")

        for (i in 0 until numResultsReturned) {
            val details = SteamUGCDetails()
            ugc.getQueryUGCResult(query, i, details)
            val publishedFileID = SteamPublishedFileID(details.getPublishedFileID().toString().toLong(16))

            ugc.downloadItem(publishedFileID, true)

            val installInfo = SteamUGC.ItemInstallInfo()

            ugc.getItemInstallInfo(publishedFileID, installInfo)

            val folder = installInfo.folder
            val sizeOnDisk = installInfo.sizeOnDisk

            //println("folder: $folder")
            //println("sizeOnDisk: $sizeOnDisk")
        }

        ugc.releaseQueryUserUGCRequest(query)
        waitingForCallback = false
    }

    override fun onSubmitItemUpdate(
        publishedFileID: SteamPublishedFileID?,
        needsToAcceptWLA: Boolean,
        result: SteamResult?
    ) {
        //println("Item Update completed: publishedFileID=$publishedFileID, needsToAcceptWLA: $needsToAcceptWLA, result=$result")
        waitingForCallback = false
    }

    fun isWaitingForCallback(): Boolean {
        return waitingForCallback
    }

    fun setWaitingForCallback(waitingForCallback: Boolean) {
        this.waitingForCallback = waitingForCallback
    }

    fun setUgc(ugc: SteamUGC) {
        this.ugc = ugc
    }
}