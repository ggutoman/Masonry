package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.LoginResult
import org.gag.appdriver.Room.Entities.EUserInfo

@Serializable
data class DownloadLogin(
    private val result: String,
    private val payload: LoginResult
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): LoginResult{
        return payload
    }
}