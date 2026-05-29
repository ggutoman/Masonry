package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity(tableName = "Position_Info", primaryKeys = ["sPositnCd"])
@Serializable
data class EPosition(
    var sPositnCd: String,
    var sPositnDs: String,
    var nLineOrdr: Int,
    var cRecdStat: String,
    var sModified: String,
    var dModified: String,
    var dTimeStmp: String
)
