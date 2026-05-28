package org.gag.appdriver.App.DataModels

import kotlinx.serialization.Serializable
import org.gag.appdriver.Room.Entities.EMemberAddress
import org.gag.appdriver.Room.Entities.EMemberContact
import org.gag.appdriver.Room.Entities.EMemberEmail
import org.gag.appdriver.Room.Entities.EMemberInfo
import org.gag.appdriver.Room.Entities.EMemberMaster

@Serializable
data class DownloadMemberInfo(

    private val result: String,
    private val payload: MemberPayload
) {

    fun getResult(): String = result
    fun getPayload(): MemberPayload = payload

    @Serializable
    data class MemberPayload(

        val member: EMemberInfo,
        val addresses: List<EMemberAddress> = emptyList(),
        val contacts: List<EMemberContact> = emptyList(),
        val emails: List<EMemberEmail> = emptyList()
    )
}