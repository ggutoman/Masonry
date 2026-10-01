package org.gag.appdriver.App.Models

import kotlinx.serialization.Serializable
import org.gag.appdriver.Room.Entities.EAnnualDetail
import org.gag.appdriver.Room.Entities.EAnnualMaster
import org.gag.appdriver.Room.Entities.EObjectPermissions
import org.gag.appdriver.Room.Entities.EUserObjects

@Serializable
data class UserObjects(
    val menu : List<EUserObjects>,
    val permission : List<EObjectPermissions>
)
