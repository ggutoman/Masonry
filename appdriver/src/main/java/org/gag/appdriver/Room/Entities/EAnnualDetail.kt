package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Annual_Detail", primaryKeys = ["sTransNox", "sMemberID"])
data class EAnnualDetail(
    val sTransNox: String,
    val nEntryNox: String,
    val sMemberID: String,
    val nAmtDuexx: String?,
    val nAmtPaidx: String?,
    val cExemptID: String?,
    val sRemarksx: String?,
    val dModified: String?,
    val dTimeStmp: String?
)
