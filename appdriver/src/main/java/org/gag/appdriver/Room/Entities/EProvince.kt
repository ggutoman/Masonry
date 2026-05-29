package org.gag.appdriver.Room.Entities

import androidx.room.PrimaryKey

data class EProvince(
    @PrimaryKey
    var sProvIDxx: String,
    var sDescript: String,
    var cRecdStat: String,
    var sModified: String,
    var dModified: String,
    var dTimeStmp: String
)
