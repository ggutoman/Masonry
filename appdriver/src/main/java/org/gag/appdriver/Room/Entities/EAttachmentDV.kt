package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "DV_Attachment", primaryKeys = ["sTransNox"])
data class EDisbursementAttachment(
    var sTransNox: String,
    var sSourceNo: String,
    var dTransact: String,
    var sDocTitle: String,
    var sRemarksx: String,
    var sFileName: String,
    var sFinFleNm: String,
    var cRecdStat: String,
    var sModified: String,
    var dModified: String
)
