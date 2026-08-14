package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.AttachmentDV
import org.gag.appdriver.App.Models.LodgeFundInfo

@Serializable
data class DownloadAttachmentDV(
    private val result: String,
    private val payload: AttachmentDV
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): AttachmentDV{
        return payload
    }
}