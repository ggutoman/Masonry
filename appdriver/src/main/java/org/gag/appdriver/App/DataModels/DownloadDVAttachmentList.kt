package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.AnnualInfo
import org.gag.appdriver.App.Models.AttachmentDV
import org.gag.appdriver.App.Models.LodgeFundInfo

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