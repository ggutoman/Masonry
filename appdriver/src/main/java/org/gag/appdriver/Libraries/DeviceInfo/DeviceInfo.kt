package org.gag.appdriver.Libraries.DeviceInfo

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

class DeviceInfo(instance : Context) {

    val context : Context = instance

    @SuppressLint("HardwareIds")
    fun GetAndroidID() : String{

        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID)
    }
}