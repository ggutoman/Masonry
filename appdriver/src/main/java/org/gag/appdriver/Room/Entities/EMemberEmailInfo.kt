package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Member_Email")
data class EMemberEmailInfo(
    @PrimaryKey
    var sMailIDxx: String,
    var sMemberID: String,
    var sEmailAdd: String,
    var cRecdStat: String?,
    var sModified: String?,
    var dModified: String?,
    var dTimeStmp: String?

)