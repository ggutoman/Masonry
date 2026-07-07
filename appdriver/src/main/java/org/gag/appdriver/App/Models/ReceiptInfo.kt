package org.gag.appdriver.App.Models

import kotlinx.serialization.Serializable
import org.gag.appdriver.Room.Entities.EAnnualDetail
import org.gag.appdriver.Room.Entities.EAnnualMaster
import org.gag.appdriver.Room.Entities.EReceiptDetail
import org.gag.appdriver.Room.Entities.EReceiptMaster

@Serializable
data class ReceiptInfo(
    val master : EReceiptMaster,
    val detail : List<EReceiptDetail>
)
