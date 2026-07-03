package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity(tableName = "Lodge_Fund_Ledger", primaryKeys = ["sLodgeIDx"])
@Serializable
data class EFundLedger(
    var sLodgeIDx: String,
    var nEntryNox: String?,
    var dTransact: String?,
    var sSourceCD: String?,
    var sSourceNo: String?,
    var nAmountIn: String?,
    var nAmountOt: String?,
    var cReversex: String?,
    var dModified: String?,
    var dTimeStmp: String?
)
