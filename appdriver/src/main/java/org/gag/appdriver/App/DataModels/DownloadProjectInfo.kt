package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.AnnualInfo
import org.gag.appdriver.App.Models.LodgeFundInfo
import org.gag.appdriver.Room.Entities.EProjectMaster

@Serializable
data class DownloadProjectInfo(
    private val result: String,
    private val payload: EProjectMaster
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): EProjectMaster{
        return payload
    }
}