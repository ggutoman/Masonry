package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Annual_Detail", primaryKeys = ["sTransNox", "sMemberID"])
data class EAnnualDetail(
    var sTransNox: String,
    var nEntryNox: String,
    var sMemberID: String,
    var nAmtDuexx: String?,
    var nAmtPaidx: String?,
    var cExemptID: String?,
    var sRemarksx: String?,
    var dModified: String?,
    var dTimeStmp: String?
)
