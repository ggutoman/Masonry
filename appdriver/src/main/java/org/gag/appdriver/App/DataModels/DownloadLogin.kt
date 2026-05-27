package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable

@Serializable
data class DownloadLogin(
    private val result: String,
    private val payload: String
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): String{
        return payload
    }
}