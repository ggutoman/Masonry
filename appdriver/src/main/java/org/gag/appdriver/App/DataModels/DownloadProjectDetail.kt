package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.AnnualInfo
import org.gag.appdriver.App.Models.LodgeFundInfo
import org.gag.appdriver.Room.Entities.EProjectDetail
import org.gag.appdriver.Room.Entities.EProjectMaster

@Serializable
data class DownloadProjectDetail(
    private val result: String,
    private val payload: List<EProjectDetail>
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): List<EProjectDetail>{
        return payload
    }
}