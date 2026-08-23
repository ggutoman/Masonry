package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity(tableName = "Sys_Reports", primaryKeys = ["idxx"])
@Serializable
data class EReports(
    var idxx : String,
    var xxParams : String,
    var xxsDescript : String,
    var xxTemplateDir : String,
    var nRightsxx : Int,
    var cRecdStat : String,
)