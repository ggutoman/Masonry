package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Member_Address")
data class EMemberAddress(
    @PrimaryKey
    val sAddrsIDx: String,

    val sMemberID: String,
    val sAddressx: String,
    val sTownIDxx: String,
    val cIsHomeAd: String,
    val cRecdStat: String,
    val sModified: String,
    val dModified: String,
    val dTimeStmp: String
)
