package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.AnnualInfo
import org.gag.appdriver.App.Models.LodgeFundInfo
import org.gag.appdriver.App.Models.MemberID

@Serializable
data class DownloadMemberID(
    private val result: String,
    private val payload: MemberID
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): MemberID {
        return payload
    }
}