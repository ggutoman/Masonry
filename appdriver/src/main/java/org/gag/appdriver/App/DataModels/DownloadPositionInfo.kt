package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.UserInfoResult
import org.gag.appdriver.Room.Entities.ELodgeInfo
import org.gag.appdriver.Room.Entities.EPosition

@Serializable
data class DownloadPositionInfo(
    private val result: String,
    private val payload: List<EPosition>
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): List<EPosition>{
        return payload
    }
}