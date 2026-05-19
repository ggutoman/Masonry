package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity(tableName = "User_Info")
@Serializable
data class EUserInfo(
    val sUserIDxx : String,
    val sLodgeIDxx : String,
    val sUsername : String,
    val sPassword : String,
    val sGLPIDNox : String,
    val sLastname : String,
    val sBrithDte : String,
    val nUserLvl : Int,
    val cRecdStat : String,
    val sModifieid : String,
    val dModified : String
)
