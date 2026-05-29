package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.UserInfoResult
import org.gag.appdriver.Room.Entities.ELodgeInfo
import org.gag.appdriver.Room.Entities.EPosition
import org.gag.appdriver.Room.Entities.ETitle

@Serializable
data class DownloadTitleInfo(
    private val result: String,
    private val payload: List<ETitle>
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): List<ETitle>{
        return payload
    }
}