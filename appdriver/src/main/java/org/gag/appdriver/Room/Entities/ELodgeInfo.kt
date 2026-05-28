package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity(tableName = "Lodge_Info", primaryKeys = ["sLodgeIDx"])
@Serializable
data class ELodgeInfo(
    var sLodgeIDx: String,
    var sLodgeNme: String,
    var sAddressx: String,
    var sTownName: String,
    var sZippCode: String,
    var sProvName: String,
    var sServerIP: String
){

}
