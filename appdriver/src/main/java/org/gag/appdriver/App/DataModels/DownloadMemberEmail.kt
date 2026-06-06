package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.UserInfoResult
import org.gag.appdriver.Room.Entities.EMemberAddress
import org.gag.appdriver.Room.Entities.EMemberContactInfo
import org.gag.appdriver.Room.Entities.EMemberEmailInfo
import org.gag.appdriver.Room.Entities.EMemberInfo

@Serializable
data class DownloadMemberEmail(
    private val result: String,
    private val payload: List<EMemberEmailInfo>
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): List<EMemberEmailInfo>{
        return payload
    }
}