package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "User_Objects", primaryKeys = ["idxx"])
data class EUserObjects(
    var idxx : String,
    var sDescriptxx : String,
    var hasParentxx : String,
    var sParentIDxx : String,
    var nRights : String
)