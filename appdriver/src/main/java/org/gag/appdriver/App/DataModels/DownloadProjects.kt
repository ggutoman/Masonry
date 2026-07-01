package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.AnnualInfo
import org.gag.appdriver.App.Models.LodgeFundInfo
import org.gag.appdriver.Room.Entities.EProjectMaster

@Serializable
data class DownloadProjects(
    private val result: String,
    private val payload: List<EProjectMaster>
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): List<EProjectMaster>{
        return payload
    }
}