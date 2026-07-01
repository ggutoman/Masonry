package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import java.io.Serializable

@kotlinx.serialization.Serializable
@Entity(tableName = "Project_Detail", primaryKeys = ["sProjctCd", "nEntryNox"])
data class EProjectDetail(
    var sProjctCd: String,
    var nEntryNox: String,
    var sMemberID: String,
    var sORNoxxxx: String,
    var dPledgexx: String,
    var nPledgexx: String,
    var nAmtPaidx: String,
    var dModified: String,
    var dTimeStmp: String
)
