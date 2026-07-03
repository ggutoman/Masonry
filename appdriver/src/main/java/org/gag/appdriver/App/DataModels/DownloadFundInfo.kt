package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.LodgeFundInfo

@Serializable
data class DownloadFundInfo(
    private val result: String,
    private val payload: LodgeFundInfo
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): LodgeFundInfo{
        return payload
    }
}