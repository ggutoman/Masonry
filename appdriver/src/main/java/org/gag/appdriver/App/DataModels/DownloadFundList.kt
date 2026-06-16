package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.Room.Entities.EFundTurnOver
import org.gag.appdriver.Room.Entities.ELodgeInfo

@Serializable
data class DownloadFundList(
    private val result: String,
    private val payload: List<EFundTurnOver>
) {

    fun GetResult(): String{
        return result
    }

    fun GetPayload(): List<EFundTurnOver>{
        return payload
    }
}