package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Annual_Master", primaryKeys = ["sTransNox"])
data class EAnnualMaster(
    val sTransNox: String,
    val sYearIDxx: String?,
    val dDueDatex: String?,
    val sRemarksx: String?,
    val nTranTotl: String?,
    val nCollTotl: String?,
    val cTranStat: String?,
    val sModified: String?,
    val dModified: String?,
    val dTimeStmp: String?
)
