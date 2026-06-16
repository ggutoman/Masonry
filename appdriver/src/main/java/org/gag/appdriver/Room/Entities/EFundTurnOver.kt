package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity(tableName = "Fund_Turnover", primaryKeys = ["sTransNox"])
@Serializable
data class EFundTurnOver(
    var sTransNox: String,
    var dTransact: String,
    var sYearIDxx: String,
    var nAmountxx: String,
    var nEndBalxx: String,
    var sRemarksx: String,
    var cTranStat: String,
    var sApproved: String,
    var dApproved: String,
    var sModified: String,
    var dModified: String,
    var dTimeStmp: String
)
