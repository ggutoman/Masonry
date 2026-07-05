package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity(tableName = "Receipts_Detail", primaryKeys = ["sTransNox", "nEntryNox"])
@Serializable
data class EReceiptDetail(
    var sTransNox: String,
    var nEntryNox: String,
    var sSourceCD: String,
    var sSourceNo: String,
    var nAmtPaidx: String,
    var dModified: String,
    var dTimeStmp: String
)
