package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.AnnualInfo
import org.gag.appdriver.App.Models.LodgeFundInfo
import org.gag.appdriver.Room.Entities.EDisbursement

@Serializable
data class DownloadDisbursementList(
    private val result: String,
    private val payload: List<EDisbursement>
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): List<EDisbursement>{
        return payload
    }
}