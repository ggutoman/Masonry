package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Member_Address")
data class EMemberAddress(
    @PrimaryKey
    var sAddrsIDx: String,
    var sMemberID: String?,
    var sAddressx: String?,
    var sTownIDxx: String?,
    var cIsHomeAd: String?,
    var cRecdStat: String?,
    var sModified: String?,
    var dModified: String?,
    var dTimeStmp: String?
)
