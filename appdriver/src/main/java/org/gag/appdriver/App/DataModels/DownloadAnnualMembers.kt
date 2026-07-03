package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.AnnualInfo
import org.gag.appdriver.App.Models.AnnualMemberInfo
import org.gag.appdriver.App.Models.LodgeFundInfo

@Serializable
data class DownloadAnnualMembers(
    private val result: String,
    private val payload: AnnualMemberInfo
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): AnnualMemberInfo{
        return payload
    }
}