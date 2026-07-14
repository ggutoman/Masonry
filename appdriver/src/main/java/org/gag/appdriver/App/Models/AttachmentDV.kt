package org.gag.appdriver.App.Models

import kotlinx.serialization.Serializable
import org.gag.appdriver.Room.Entities.EAnnualDetail
import org.gag.appdriver.Room.Entities.EAnnualMaster
import org.gag.appdriver.Room.Entities.EDisbursementAttachment

@Serializable
data class AttachmentDV(
    val attachment_info : EDisbursementAttachment,
    val file_meta : String
)
