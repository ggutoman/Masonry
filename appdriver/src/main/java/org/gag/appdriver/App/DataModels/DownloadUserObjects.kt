package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.LodgeFundInfo
import org.gag.appdriver.App.Models.UserObjects
import org.gag.appdriver.Room.Entities.EUserObjects

@Serializable
data class DownloadUserObjects(
    private val result: String,
    private val payload: UserObjects
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): UserObjects{
        return payload
    }
}