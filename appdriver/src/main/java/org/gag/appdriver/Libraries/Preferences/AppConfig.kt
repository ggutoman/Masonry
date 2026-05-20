package org.gag.appdriver.Libraries.Preferences

import android.content.Context
import android.content.SharedPreferences

class AppConfig(instance : Context) {

    val sharedPref : SharedPreferences = instance.getSharedPreferences(
        "Masonry_Config",
        0
    )

    val prefEditor : SharedPreferences.Editor = sharedPref.edit()

    val IS_INITIALIZED : String = "cInitialized"
    val IS_LOGIN : String = "cLoggedIn"
    val LOG_DATE : String = "sLogDate"
    val PRODUCT_ID : String = "sPrdctID"
    val MEMBER_ID : String = "sMbrID"
    val DEVICE_ID : String = "sDvcID"
    val TOKEN_ID : String = "sTknID"

    fun isLogIn(fsVal : String){
        prefEditor.putString(IS_LOGIN, fsVal)
        prefEditor.commit()
    }

    fun hasLoggedIn() : Boolean{
        return sharedPref.getString(IS_LOGIN, "0").equals("1")
    }

    fun setLogDate(fsVal : String){
        prefEditor.putString(LOG_DATE, fsVal)
        prefEditor.commit()
    }

    fun getLogDate() : String{
        return sharedPref.getString(LOG_DATE, "1900-00-00").toString()
    }

    fun isInitialize(fsVal : String){
        prefEditor.putString(IS_INITIALIZED, fsVal)
        prefEditor.commit()
    }

    fun hasInitialized() : Boolean{
        return sharedPref.getString(IS_INITIALIZED, "0").equals("1")
    }

    fun setProductID(fsVal : String){
        prefEditor.putString(PRODUCT_ID, fsVal)
        prefEditor.commit()
    }

    fun getProductID() : String{
        return sharedPref.getString(PRODUCT_ID, "MSNRY_APP").toString()
    }

    fun setMemberID(fsVal : String){
        prefEditor.putString(MEMBER_ID, fsVal)
        prefEditor.commit()
    }

    fun getMemberID() : String{
        return sharedPref.getString(MEMBER_ID, "").toString()
    }

    fun setDeviceID(fsVal : String){
        prefEditor.putString(DEVICE_ID, fsVal)
        prefEditor.commit()
    }

    fun getDeviceID() : String{
        return sharedPref.getString(DEVICE_ID, "").toString()
    }

    fun setTokenID(fsVal : String){
        prefEditor.putString(TOKEN_ID, fsVal)
        prefEditor.commit()
    }

    fun getokenID() : String{
        return sharedPref.getString(TOKEN_ID, "").toString()
    }

}