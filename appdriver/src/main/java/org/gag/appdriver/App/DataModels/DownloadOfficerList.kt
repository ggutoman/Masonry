package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.UserInfoResult
import org.gag.appdriver.Room.Entities.EMemberInfo
import org.gag.appdriver.Room.Entities.EOfficer

@Serializable
data class DownloadOfficerList(
    private val result: String,
    private val payload: List<EOfficer>
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): List<EOfficer>{
        return payload
    }
}