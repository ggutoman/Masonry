package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity(tableName = "Lodge_Calendar", primaryKeys = ["sYearIDxx"])
@Serializable
data class ELodgeCalendar(
    var sYearIDxx: String,
    var sLodgeIDx: String,
    var nYearxxxx: String,
    var dFromDate: String,
    var dThruDate: String,
    var dModified: String,
    var dTimeStmp: String
){

}
