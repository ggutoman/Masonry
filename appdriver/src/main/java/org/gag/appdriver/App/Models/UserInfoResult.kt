package org.gag.appdriver.App.Models

import kotlinx.serialization.Serializable
import org.gag.appdriver.Room.Entities.EMemberInfo
import org.gag.appdriver.Room.Entities.EOfficer
import org.gag.appdriver.Room.Entities.EPosition
import org.gag.appdriver.Room.Entities.EUserInfo

@Serializable
data class UserInfoResult(
    var user_info : EUserInfo,
    var member_info : EMemberInfo,
    var officer_info : EOfficer,
    var position_info : EPosition
){
}