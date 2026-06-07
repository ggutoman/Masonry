package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "Officer_History")
@Serializable
data class EOfficerHistory(
    @PrimaryKey
    var sTransNox: String,
    var dTransact: String,
    var sYearIDxx: String,
    var sMemberID: String,
    var sPositnCd: String,
    var cOldStatx: String,
    var cNewStatx: String,
    var sRemarksx: String?,
    var sModified: String,
    var dModified: String,
    var dTimeStmp: String?
)
