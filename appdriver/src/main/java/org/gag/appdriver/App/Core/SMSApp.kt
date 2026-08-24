package org.gag.appdriver.App.Core

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import org.gag.appdriver.App.Interface.OnSMSStatus
import org.gag.appdriver.App.Service.DataReceiver

class SMSApp(val loInstance : Context, val foCallback : OnSMSStatus){

    val FLAG_SENT : String = "SMS_SENT"
    val FLAG_DELIVERED : String = "SMS_DELIVERED"

    private val smsManager: SmsManager by lazy {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            SmsManager.getDefault()
        } else {
            loInstance.getSystemService(SmsManager::class.java)
                ?: throw IllegalStateException("SmsManager not available")
        }
    }

    fun SendSms(phoneNumber: String, message: String) {

        try {

            // Create intents with extras
            val sentIntent = Intent(FLAG_SENT).apply {
                putExtra("id", "SMS")
                putExtra("status", "SMS_SENT")
            }
            val deliveredIntent = Intent(FLAG_DELIVERED).apply {
                putExtra("id", "SMS")
                putExtra("status", "SMS_DELIVERED")
            }

            // Create PendingIntents
            val sentPI = PendingIntent.getBroadcast(
                loInstance, 0, sentIntent, PendingIntent.FLAG_IMMUTABLE
            )
            val deliveredPI = PendingIntent.getBroadcast(
                loInstance, 0, deliveredIntent, PendingIntent.FLAG_IMMUTABLE
            )

            // Register your DataReceiver for both actions
            val sentReceiver = DataReceiver(foCallback)
            val deliveredReceiver = DataReceiver(foCallback)

            ContextCompat.registerReceiver(
                loInstance,
                sentReceiver,
                IntentFilter(FLAG_SENT),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )

            ContextCompat.registerReceiver(
                loInstance,
                deliveredReceiver,
                IntentFilter(FLAG_DELIVERED),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )

            // Send SMS
            smsManager.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI)

            foCallback.OnResult("SMS has been sent")
        }catch (ex : Exception){
            foCallback.OnResult(ex.message.toString())
        }
    }

}