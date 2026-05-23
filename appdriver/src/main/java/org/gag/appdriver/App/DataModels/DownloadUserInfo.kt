package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.Room.Entities.EUserInfo

@Serializable
data class DownloadUserInfo(
    private val status: String,
    private val result: EUserInfo
) {

    fun GetStatus(): String{
        return status
    }

    fun GetData(): EUserInfo{
        return result
    }
}