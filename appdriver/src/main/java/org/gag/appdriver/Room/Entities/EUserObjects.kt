package org.gag.appdriver.Room.Entities

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
@Entity(tableName = "User_Objects", primaryKeys = ["idxx"])
data class EUserObjects(
    var idxx : String,
    var sDescriptxx : String,
    var hasParentxx : String,
    var sParentIDxx : String,
    var nRights : String,
    var cRecdStat : String
) : Parcelable