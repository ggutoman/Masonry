package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Object_Permissions", primaryKeys = ["object_id", "owner_id"])
data class EObjectPermissions(
    var object_id: String,
    var owner_id: String,
    var cRecdStat: String
)
