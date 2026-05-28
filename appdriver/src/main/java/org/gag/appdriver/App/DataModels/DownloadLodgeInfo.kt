package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.UserInfoResult
import org.gag.appdriver.Room.Entities.ELodgeInfo

@Serializable
data class DownloadLodgeInfo(
    private val result: String,
    private val payload: ELodgeInfo
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): ELodgeInfo{
        return payload
    }
}