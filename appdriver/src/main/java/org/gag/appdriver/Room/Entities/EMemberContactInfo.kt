package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Member_Contact")
data class EMemberContactInfo(
    @PrimaryKey
    val sContctID: String,     // varchar(10)

    val sMemberID: String,     // varchar(10)
    val sContctNo: String,     // varchar(13)
    val sRemarksx: String,     // varchar(50)
    val cRecdStat: String,     // char(1)
    val sModified: String,     // varchar(32)
    val dModified: String,     // datetime
    val dTimeStmp: String      // TIMESTAMP

)
