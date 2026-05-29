package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity(tableName = "Title_Info", primaryKeys = ["sTitleIDx"])
@Serializable
data class ETitle(
    var sTitleIDx: String,
    var sTitleDsc: String,
    var cRecdStat: String,
    var sModified: String,
    var dModified: String,
    var dTimeStmp: String
)
