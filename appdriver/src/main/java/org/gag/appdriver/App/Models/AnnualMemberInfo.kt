package org.gag.appdriver.App.Models

import kotlinx.serialization.Serializable
import org.gag.appdriver.Room.Entities.EAnnualDetail
import org.gag.appdriver.Room.Entities.EAnnualMaster

@Serializable
data class AnnualMemberInfo(
    val master : List<EAnnualMaster>,
    val detail : List<EAnnualDetail>
)
