package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Member_Contact")
data class EMemberContactInfo(
    @PrimaryKey
    var sContctID: String,     // varchar(10)

    var sMemberID: String,     // varchar(10)
    var sContctNo: String,     // varchar(13)
    var sRemarksx: String?,     // varchar(50)
    var cRecdStat: String?,     // char(1)
    var sModified: String?,     // varchar(32)
    var dModified: String?,     // datetime
    var dTimeStmp: String?     // TIMESTAMP

)
