package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "Member_Email",
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
data class EMemberEmail(

    @PrimaryKey
    val sMailIDxx: String,

    val sMemberID: String,
    val sEmailAdd: String?,
    val cRecdStat: String?
)