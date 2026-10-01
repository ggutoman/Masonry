package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.LodgeFundInfo
import org.gag.appdriver.App.Models.ReceiptInfo
import org.gag.appdriver.Room.Entities.ESysNotifs

@Serializable
data class DownloadNotifs(
    private val result: String,
    private val payload: List<ESysNotifs>
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): List<ESysNotifs>{
        return payload
    }
}