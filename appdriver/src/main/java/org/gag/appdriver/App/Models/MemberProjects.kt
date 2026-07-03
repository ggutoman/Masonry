package org.gag.appdriver.App.Models

import kotlinx.serialization.Serializable
import org.gag.appdriver.Room.Entities.EAnnualDetail
import org.gag.appdriver.Room.Entities.EAnnualMaster
import org.gag.appdriver.Room.Entities.EProjectDetail
import org.gag.appdriver.Room.Entities.EProjectMaster

@Serializable
data class MemberProjects(
    val master : List<EProjectMaster>,
    val detail : List<EProjectDetail>
)
