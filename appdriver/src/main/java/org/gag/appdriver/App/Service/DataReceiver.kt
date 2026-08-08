package org.gag.appdriver.App.Service

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import org.gag.appdriver.App.Interface.OnSMSStatus

class DataReceiver(val poResult: OnSMSStatus) : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {

        p1?.extras?.let { bundle: Bundle ->

            val intent_id = bundle.getString("id") ?: "unknown"

            when(intent_id){

                "SMS" -> {

                    val sms_status = bundle.getString("status") ?: "unknown"
                    if (sms_status.equals("SMS_SENT")){

                        when(resultCode){

                            Activity.RESULT_OK-> poResult.OnResult("SMS sent successfully")
                            Activity.RESULT_CANCELED-> poResult.OnResult("SMS was cancelled")
                            SmsManager.RESULT_ERROR_GENERIC_FAILURE -> poResult.OnResult("Generic failure")
                            SmsManager.RESULT_ERROR_NO_SERVICE  -> poResult.OnResult("No service")
                            SmsManager.RESULT_ERROR_NULL_PDU  -> poResult.OnResult("Null PDU")
                            SmsManager.RESULT_ERROR_RADIO_OFF  -> poResult.OnResult("Radio Off")
                        }
                    }else if (sms_status.equals("SMS_DELIVERED")) {

                        when(resultCode){

                            Activity.RESULT_OK-> poResult.OnResult("SMS sent successfully")
                            Activity.RESULT_CANCELED-> poResult.OnResult("SMS cancelled")
                        }

                    }
                    p0?.unregisterReceiver(this)
                }

                else -> Toast.makeText(p0, "Unkown data received",Toast.LENGTH_SHORT).show()
            }
        }
    }
}