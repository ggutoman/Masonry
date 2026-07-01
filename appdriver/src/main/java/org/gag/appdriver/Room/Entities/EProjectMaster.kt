package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Project_Master", primaryKeys = ["sProjctCd"])
data class EProjectMaster(
    var sProjctCd: String,
    var sProjctNm: String,
    var cProjctTp: String,
    var sYearIDxx: String,
    var dTransact: String,
    var dDueDatex: String,
    var sRemarksx: String,
    var dAccompls: String,
    var nTranTotl: String,
    var nCollTotl: String,
    var nDisbTotl: String,
    var cTranStat: String,
    var sModified: String,
    var dModified: String,
    var dTimeStmp: String
)
