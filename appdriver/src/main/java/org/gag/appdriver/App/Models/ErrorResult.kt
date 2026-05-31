package org.gag.appdriver.App.Models

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResult(
    var errno : Int,
    var message : String
){

}