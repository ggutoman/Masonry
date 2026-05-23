package com.example.autodealersapplication.android.DataModels

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    private val result: String,
    private val message: String
){
    fun GetResult(): String{
        return result
    }

    fun GetError(): String{
        return message
    }

}