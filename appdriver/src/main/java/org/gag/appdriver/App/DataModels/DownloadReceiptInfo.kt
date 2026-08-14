package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.LodgeFundInfo
import org.gag.appdriver.App.Models.ReceiptInfo

@Serializable
data class DownloadReceiptInfo(
    private val result: String,
    private val payload: ReceiptInfo
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): ReceiptInfo{
        return payload
    }
}