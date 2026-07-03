package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.AnnualInfo
import org.gag.appdriver.App.Models.LodgeFundInfo

@Serializable
data class DownloadAnnualInfo(
    private val result: String,
    private val payload: AnnualInfo
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): AnnualInfo{
        return payload
    }
}