package org.gag.appdriver.App.Models

import kotlinx.serialization.Serializable

@Serializable
data class LoginResult(
    var sUserIDxx : String,
    var sUserName : String,
    var sPassword : String,
    var sLodgeIDxx : String,
    var sGLPIDNoX : String,
    var sLastName : String,
    var dBirthDte : String,
    var nUserLevl : Int,
    var cRecdStat : String,
    var sModified : String,
    var dModified : String,
    var sTokenIDxx : String
){

}