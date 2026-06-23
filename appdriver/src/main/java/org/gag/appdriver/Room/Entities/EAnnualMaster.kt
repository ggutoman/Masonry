package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Annual_Master", primaryKeys = ["sTransNox"])
data class EAnnualMaster(
    var sTransNox: String,
    var sYearIDxx: String,
    var dDueDatex: String,
    var sRemarksx: String,
    var nTranTotl: String,
    var nCollTotl: String,
    var cTranStat: String,
    var sModified: String,
    var dModified: String,
    var dTimeStmp: String
)
