package org.gag.appdriver.App.Models

import kotlinx.serialization.Serializable

@Serializable
data class SaveFundResult(
    var transactid : String,
    var endbalance : Double
){

}