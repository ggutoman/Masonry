package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.LodgeFundInfo
import org.gag.appdriver.App.Models.ReceiptInfo
import org.gag.appdriver.App.Models.ReceiptList

@Serializable
data class DownloadReceiptList(
    private val result: String,
    private val payload: ReceiptList
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): ReceiptList{
        return payload
    }
}