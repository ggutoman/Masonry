package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "Title")
@Serializable
data class ETitle(

    @PrimaryKey
    val sTitleIDx: String,

    val sTitleDsc: String?,
    val cRecdStat: String?,
    val sModified: String?,
    val dModified: String?,
    val dTimeStmp: String?
)