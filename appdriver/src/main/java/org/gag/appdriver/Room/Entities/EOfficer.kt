package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity(tableName = "Officer_Info", primaryKeys = ["sYearIDxx", "sMemberID"])
@Serializable
data class EOfficer(
    var sYearIDxx: String,
    var sPositnCd: String?,
    var sMemberID: String,
    var cAppointx: String?,
    var cStatusxx: String?,
    var sModified: String?,
    var dModified: String?,
    var dTimeStmp: String?
)