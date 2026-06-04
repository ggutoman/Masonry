package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.UserInfoResult
import org.gag.appdriver.Room.Entities.EMemberInfo

@Serializable
data class DownloadMemberList(
    private val result: String,
    private val payload: List<EMemberInfo>
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): List<EMemberInfo>{
        return payload
    }
}