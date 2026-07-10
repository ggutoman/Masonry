package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.AnnualInfo
import org.gag.appdriver.App.Models.LodgeFundInfo
import org.gag.appdriver.Room.Entities.EDisbursement

@Serializable
data class DownloadDisbursement(
    private val result: String,
    private val payload: EDisbursement
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): EDisbursement{
        return payload
    }
}