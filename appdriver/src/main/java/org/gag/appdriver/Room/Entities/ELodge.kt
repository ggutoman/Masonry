package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "xxxLodgeInfo")
@Serializable
data class ELodge(

    @PrimaryKey
    val sLodgeIDx: String,

    val sLodgeNme: String?,
    val sAddressx: String?,
    val sTownName: String?,
    val sZipCpde: String?,
    val sProvName: String?,
    val sServerIP: String?,
    val dTimeStmp: String?
)