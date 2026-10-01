package org.gag.appdriver.App.Models

import kotlinx.serialization.Serializable
import org.gag.appdriver.Room.Entities.EObjectPermissions
import org.gag.appdriver.Room.Entities.EReports

@Serializable
data class ReportObjects(
    val attachment : List<EReports>,
    val permission : List<EObjectPermissions>
)