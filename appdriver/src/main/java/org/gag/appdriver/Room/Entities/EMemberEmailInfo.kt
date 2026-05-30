package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Email_Info")
data class EMemberEmailInfo(
    @PrimaryKey
    val sMailIDxx: String,
    val sMemberID: String,
    val sEmailAdd: String,
    val cRecdStat: String,
    val sModified: String,
    val dModified: String,
    val dTimeStmp: String

)