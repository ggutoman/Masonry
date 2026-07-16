package org.gag.appdriver.App.Models

import kotlinx.serialization.Serializable
import org.gag.appdriver.Room.Entities.EDisbursement
import org.gag.appdriver.Room.Entities.EDisbursementAttachment

@Serializable
data class DisbursementInfo(
    val master : EDisbursement,
    val attachments : List<EDisbursementAttachment>
)
