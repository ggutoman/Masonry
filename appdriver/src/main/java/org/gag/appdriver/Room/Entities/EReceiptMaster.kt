package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "Receipts_Master")
@Serializable
data class EReceiptMaster(
    @PrimaryKey
    var sTransNox: String,
    var dTransact: String,
    var sMemberID: String,
    var cPaymForm: String,
    var nTotlPaid: String,
    var sORNoxxxx: String,
    var sRemarksx: String,
    var cSendStat: String,
    var cTranStat: String,
    var sModified: String,
    var dModified: String,
    var dTimeStmp: String
)
