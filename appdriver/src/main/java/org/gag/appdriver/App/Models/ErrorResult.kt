package org.gag.appdriver.App.Models

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResult(
    var errno : String,
    var message : String
){

}