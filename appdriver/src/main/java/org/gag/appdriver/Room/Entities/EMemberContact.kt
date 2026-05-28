package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "Member_Contact",
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
data class EMemberContact(

    @PrimaryKey
    val sContctID: String,

    val sMemberID: String,
    val sContctNo: String?,
    val sRemarksx: String?,
    val cRecdStat: String?
)