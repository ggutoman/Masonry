package org.gag.appdriver.App.Core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AppReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {

            val serviceIntent = Intent(it, DataSyncService::class.java).apply {
                action = intent?.action
                putExtras(intent ?: Intent())
            }
            it.startService(serviceIntent)
        }
    }
}
