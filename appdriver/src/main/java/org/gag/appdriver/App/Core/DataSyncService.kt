package org.gag.appdriver.App.Core

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture

class DataSyncService : Service(){

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        CoroutineScope(Dispatchers.IO).launch {
            intent?.let { loIntent ->
                if (loIntent.action == "LOGIN_ACTION" && loIntent.hasExtra("user_id")) {
                    val userId = loIntent.getStringExtra("user_id")

                    if (!userId.isNullOrEmpty()) {

                        // Step 1: Lodge tasks
                        Lodge(applicationContext).apply {

                            val poAccount = UserAccount(applicationContext)
                            DownloadLodgeInfo().thenAccept { lodgeSuccess ->

                                //continue syncing data if lodge information is successfully downloaded
                                if (lodgeSuccess) {

                                    //collect other tasks after success download of lodge
                                    val laSyncData: HashSet<CompletableFuture<Boolean>> = hashSetOf(
                                        SaveLodgeCalendar(),
                                        poAccount.DownloadMemberList("2026-01-01", "2026-12-01"),
                                        poAccount.DownloadOfficerList("2026-01-01", "2026-12-01")
                                    )

                                    //execute per task, and print per message if error occured
                                    for (loTask in laSyncData) {
                                        loTask.thenAccept { chainTask ->
                                            if (!chainTask) {

                                                val error = if (poAccount.message.isNullOrEmpty()) message else message
                                                println(error)
                                            }
                                        }

                                    }

                                }

                            }
                        }
                    }
                }
            }

            // Stop service once tasks are done
            stopSelf(startId)
        }
        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}