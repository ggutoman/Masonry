package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity(tableName = "User_Info", primaryKeys = ["sUserIDxx"])
@Serializable
data class EUserInfo(
    var sUserIDxx : String,
    var sUserName : String,
    var sPassword : String,
    var sLodgeIDxx : String,
    var sGLPIDNoX : String,
    var sLastName : String,
    var dBirthDte : String,
    var nUserLevl : Int,
    var cRecdStat : String,
    var sModified : String,
    var dModified : String
){

}
