package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "xxSysNotifxx", primaryKeys = ["sTransNox"])
data class ESysNotifs(
    var sTransNox: String,
    var dTransact: String,
    var sRcvrIDxx: String,
    var sSourcexx: String,
    var sSourceNo: String,
    var sTitlex: String,
    var sMessagex: String,
    var cReadxxxx: String,
    var dReadxxxx: String?,
    var dModified: String?
)
