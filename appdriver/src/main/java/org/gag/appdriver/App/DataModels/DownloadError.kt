package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.ErrorResult

@Serializable
data class DownloadError(
    private val result: String,
    private val payload: ErrorResult
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): ErrorResult{
        return payload
    }
}