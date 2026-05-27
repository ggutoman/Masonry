package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.UserInfoResult

@Serializable
data class DownloadUserInfo(
    private val result: String,
    private val payload: UserInfoResult
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): UserInfoResult{
        return payload
    }
}