package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.UserInfoResult
import org.gag.appdriver.Room.Entities.EMemberAddress
import org.gag.appdriver.Room.Entities.EMemberInfo

@Serializable
data class DownloadMemberAddresses(
    private val result: String,
    private val payload: List<EMemberAddress>
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): List<EMemberAddress>{
        return payload
    }
}