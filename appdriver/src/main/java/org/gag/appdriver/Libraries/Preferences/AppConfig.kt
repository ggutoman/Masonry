package org.gag.appdriver.Libraries.Preferences

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit

class AppConfig(instance : Context) {

    val sharedPref : SharedPreferences = instance.getSharedPreferences(
        "Masonry_Config",
        MODE_PRIVATE
    )

    val IS_INITIALIZED : String = "cInitialized"
    val IS_LOGIN : String = "cLoggedIn"
    val LOG_DATE : String = "sLogDate"
    val PRODUCT_ID : String = "sPrdctID"
    val DEVICE_ID : String = "sDvcID"
    val TOKEN_ID : String = "sTknID"

    fun isLogIn(fsVal : String){
        sharedPref.edit(commit = true) { putString(IS_LOGIN, fsVal)}
    }

    fun hasLoggedIn() : Boolean{
        return sharedPref.getString(IS_LOGIN, "0").equals("1")
    }

    fun setLogDate(fsVal : String){
        sharedPref.edit(commit = true) { putString(LOG_DATE, fsVal) }
    }

    fun getLogDate() : String{
        return sharedPref.getString(LOG_DATE, "1900-00-00").toString()
    }

    fun isInitialize(fsVal : String){
        sharedPref.edit(commit = true) { putString(IS_INITIALIZED, fsVal) }
    }

    fun hasInitialized() : Boolean{
        return sharedPref.getString(IS_INITIALIZED, "0").equals("1")
    }

    fun setProductID(fsVal : String){
        sharedPref.edit(commit = true) { putString(PRODUCT_ID, fsVal) }
    }

    fun getProductID() : String{
        return sharedPref.getString(PRODUCT_ID, "MSNRY_APP").toString()
    }

    fun setDeviceID(fsVal : String){
        sharedPref.edit(commit = true) { putString(DEVICE_ID, fsVal)}
    }

    fun getDeviceID() : String{
        return sharedPref.getString(DEVICE_ID, "").toString()
    }

    fun setTokenID(fsVal : String){
        sharedPref.edit(commit = true) { putString(TOKEN_ID, fsVal) }
    }

    fun getokenID() : String{
        return sharedPref.getString(TOKEN_ID, "").toString()
    }

    fun ClearAccountSession(){
        sharedPref.edit(commit = true) { putString(IS_LOGIN, "0") }
        sharedPref.edit(commit = true) { putString(TOKEN_ID, "")}
    }

}