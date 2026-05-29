package org.gag.appdriver.Room.DataObject

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "Province")
@Serializable
data class EProvinceInfo(
    @PrimaryKey
    var sProvIDxx: String,
    var sDescript: String,
    var cRecdStat: String,
    var sModified: String,
    var dModified: String,
    var dTimeStmp: String
)
