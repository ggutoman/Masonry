package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.UserInfoResult
import org.gag.appdriver.Room.Entities.ETownCity

@Serializable
data class DownloadTownInfo(
    private val result: String,
    private val payload: List<ETownCity>
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): List<ETownCity>{
        return payload
    }
}