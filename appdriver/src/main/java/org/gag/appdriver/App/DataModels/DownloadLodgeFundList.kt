package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.LodgeFundInfo
import org.gag.appdriver.Room.Entities.EFundMaster

@Serializable
data class DownloadLodgeFundList(
    private val result: String,
    private val payload: List<EFundMaster>
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): List<EFundMaster>{
        return payload
    }
}