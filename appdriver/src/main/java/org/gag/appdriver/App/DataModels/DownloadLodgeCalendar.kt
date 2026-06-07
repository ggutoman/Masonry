package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.UserInfoResult
import org.gag.appdriver.Room.Entities.ELodgeCalendar

@Serializable
data class DownloadLodgeCalendar(
    private val result: String,
    private val payload: List<ELodgeCalendar>
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): List<ELodgeCalendar>{
        return payload
    }
}