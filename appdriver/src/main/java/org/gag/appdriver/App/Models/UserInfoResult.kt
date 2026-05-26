package org.gag.appdriver.App.Models

import kotlinx.serialization.Serializable
import org.gag.appdriver.Room.Entities.EMemberInfo
import org.gag.appdriver.Room.Entities.EUserInfo

@Serializable
data class UserInfoResult(
    var userIinfo : EUserInfo,
    var memberInfo : EMemberInfo
){

}