package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "Member_Address",
    foreignKeys = [
        ForeignKey(
            entity = EMemberMaster::class,
            parentColumns = ["sMemberID"],
            childColumns = ["sMemberID"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sMemberID")]
)
@Serializable
data class EMemberAddress(

    @PrimaryKey
    val sAddrsIDx: String,

    val sMemberID: String,
    val sAddressx: String?,
    val sTownIDxx: String?,
    val cIsHomeAd: String?,
    val cRecdStat: String?,
    val sModified: String?,
    val dModified: String?
)