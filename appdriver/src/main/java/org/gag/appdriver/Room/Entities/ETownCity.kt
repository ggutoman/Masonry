package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "Town_City")
@Serializable
data class ETownCity(
    @PrimaryKey
    var sTownIDxx: String,
    var sTownName: String,
    var sProvIDxx: String,
    var cRecdStat: String,
    var sModified: String,
    var dModified: String,
    var dTimeStmp: String
)

