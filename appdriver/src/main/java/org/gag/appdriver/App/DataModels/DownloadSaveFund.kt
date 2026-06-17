package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.SaveFundResult

@Serializable
data class DownloadSaveFund(
    private val result: String,
    private val payload: SaveFundResult
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): SaveFundResult{
        return payload
    }
}