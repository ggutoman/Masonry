package org.gag.appdriver.App.Service

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast

class DataReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {

        p1?.extras?.let { bundle: Bundle ->

            val intent_id = bundle.getString("id") ?: "unknown"

            when(intent_id){

                "SMS" -> {

                    val sms_status = bundle.getString("status") ?: "unknown"
                    if (sms_status.equals("SMS_SENT")){

                        when(resultCode){

                            Activity.RESULT_OK-> Toast.makeText(p0, "SMS sent successfully",Toast.LENGTH_SHORT).show()
                            Activity.RESULT_CANCELED-> Toast.makeText(p0, "SMS cancelled",Toast.LENGTH_SHORT).show()
                            SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(p0, "Generic Failure",Toast.LENGTH_SHORT).show()
                            SmsManager.RESULT_ERROR_NO_SERVICE  -> Toast.makeText(p0, "No Service",Toast.LENGTH_SHORT).show()
                            SmsManager.RESULT_ERROR_NULL_PDU  -> Toast.makeText(p0, "Null PDU",Toast.LENGTH_SHORT).show()
                            SmsManager.RESULT_ERROR_RADIO_OFF  -> Toast.makeText(p0, "Radio Off",Toast.LENGTH_SHORT).show()
                        }
                    }else if (sms_status.equals("SMS_DELIVERED")) {

                        when(resultCode){

                            Activity.RESULT_OK-> Toast.makeText(p0, "SMS sent successfully",Toast.LENGTH_SHORT).show()
                            Activity.RESULT_CANCELED-> Toast.makeText(p0, "SMS cancelled",Toast.LENGTH_SHORT).show()
                        }

                    }
                    p0?.unregisterReceiver(this)
                }

                else -> Toast.makeText(p0, "Unkown data received",Toast.LENGTH_SHORT).show()
            }
        }
    }
}