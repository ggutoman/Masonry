package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity(tableName = "Lodge_Fund_Master", primaryKeys = ["sLodgeIDx"])
@Serializable
data class EFundMaster(
    var sLodgeIDx: String,
    var dBegDatex: String?,
    var nBegBalxx: String?,
    var nBalancex: String?,
    var dModified: String?,
    var dTimeStmp: String?
)
