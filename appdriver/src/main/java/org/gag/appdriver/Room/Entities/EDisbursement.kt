package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Disbursement", primaryKeys = ["sTransNox"])
data class EDisbursement(
    var sTransNox: String,
    var dTransact: String,
    var sSourceCD: String,
    var sSourceNo: String,
    var sPayeeNme: String,
    var sParticlr: String,
    var sVouchrNo: String,
    var nAmountxx: String,
    var cTranStat: String,
    var sApproved: String,
    var dApproved: String,
    var dReleased: String,
    var dComplete: String,
    var sModified: String,
    var dModified: String
)
