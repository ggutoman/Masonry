package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.AnnualInfo
import org.gag.appdriver.App.Models.LodgeFundInfo
import org.gag.appdriver.Room.Entities.EUserObjects

@Serializable
data class DownloadUserObjects(
    private val result: String,
    private val payload: List<EUserObjects>
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): List<EUserObjects>{
        return payload
    }
}