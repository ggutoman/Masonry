package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.AttachmentDV

@Serializable
data class DownloadDVAttachmentList(
    private val result: String,
    private val payload: List<AttachmentDV>
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): List<AttachmentDV>{
        return payload
    }
}