package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.UserInfoResult
import org.gag.appdriver.Room.Entities.EMemberInfo
import org.gag.appdriver.Room.Entities.EOfficer
import org.gag.appdriver.Room.Entities.EOfficerHistory

@Serializable
data class DownloadOfficerHistory(
    private val result: String,
    private val payload: List<EOfficerHistory>
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): List<EOfficerHistory>{
        return payload
    }
}