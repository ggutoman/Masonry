package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.App.Models.UserInfoResult
import org.gag.appdriver.Room.DataObject.EProvinceInfo

@Serializable
data class DownloadProvinceInfo(
    private val result: String,
    private val payload: List<EProvinceInfo>
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): List<EProvinceInfo>{
        return payload
    }
}