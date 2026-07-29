package org.gag.appdriver.App.Core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                intent?.let { loIntent ->

                    //verify intent receiver
                    if (loIntent.action.equals("LOGIN_ACTION")){

                        //if login, check user id
                        if (loIntent.hasExtra("user_id")) {

                            val userId = loIntent.getStringExtra("user_id")
                            if (!userId.isNullOrEmpty() && context != null) {

                                // Step 1: Lodge tasks
                                Lodge(context).apply {
                                    DownloadLodgeInfo()
                                    SaveLodgeCalendar()
                                }

                                // Step 2: UserAccount tasks (sequential)
                                UserAccount(context).apply {
                                    DownloadMemberList("2026-01-01", "2026-12-01")
                                    DownloadOfficerList("2026-01-01", "2026-12-01")
                                }
                            }
                        }

                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
