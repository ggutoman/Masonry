package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.AnnualMemberInfo
import org.gag.appdriver.Room.Entities.EReports

@Serializable
data class DownloadReportObjects(
    private val result: String,
    private val payload: List<EReports>
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): List<EReports>{
        return payload
    }
}