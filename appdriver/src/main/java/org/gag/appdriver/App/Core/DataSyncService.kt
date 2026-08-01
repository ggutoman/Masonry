package org.gag.appdriver.App.Core

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.gag.appdriver.Libraries.DateUtil.DateRepository
import java.util.concurrent.CompletableFuture

class DataSyncService : Service(){

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        CoroutineScope(Dispatchers.IO).launch {
            intent?.let { loIntent ->

                if (loIntent.action == "LOGIN_ACTION") {

                    if (!(loIntent.hasExtra("user_id") && loIntent.hasExtra("member_id"))){
                        Toast.makeText(applicationContext, "User information not found", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    val userId = loIntent.getStringExtra("user_id")
                    val memberId = loIntent.getStringExtra("member_id")

                    if (!userId.isNullOrEmpty()) {

                        // Step 1: Lodge tasks
                        Lodge(applicationContext).apply {

                            val poAccount = UserAccount(applicationContext)
                            DownloadLodgeInfo().thenCompose { lodgeSuccess ->

                                //continue syncing data if lodge information is successfully downloaded
                                if (lodgeSuccess) {

                                    //collect other tasks after success download of lodge
                                    val laSyncData: HashSet<CompletableFuture<Boolean>> = hashSetOf(
                                        SaveLodgeFund(),
                                        poAccount.DownloadMemberList(),
                                        poAccount.DownloadOfficerList(),
                                        poAccount.DownloadUserObjects()
                                    )

                                    //execute per task, and print per message if error occured
                                    var chainResult = CompletableFuture.completedFuture(true);
                                    for (loTask in laSyncData) {

                                        chainResult = chainResult.thenCompose { aBoolean ->
                                            if (!aBoolean){
                                                Log.d("Sync Account", poAccount.message)
                                                Log.d("Sync Data", message)
                                                return@thenCompose CompletableFuture.completedFuture(false)
                                            }
                                            return@thenCompose loTask
                                        }
                                    }
                                    return@thenCompose chainResult
                                }
                                CompletableFuture.completedFuture(false)

                            }.thenAccept { syncSuccess ->

                                if (syncSuccess){

                                    //save lodge calendar
                                    SaveLodgeCalendar().thenAccept { calendarSuccess ->

                                        if (calendarSuccess is Boolean){

                                            if (!calendarSuccess){
                                                Log.d("Sync Calendar Data", message)
                                                return@thenAccept
                                            }
                                        }else if (calendarSuccess is String){

                                            if (calendarSuccess.isEmpty()){
                                                Log.d("Save Calendar Data", message)
                                                return@thenAccept
                                            }

                                            //create annual due for the year id
                                            val poAnnual = Annual(applicationContext)
                                            poAnnual.SaveAnnualDue(calendarSuccess, memberId.toString()).thenAccept { annualSuccess ->
                                                if (!annualSuccess){
                                                    Log.d("Save Annual", message)
                                                    return@thenAccept
                                                }
                                            }

                                        }else{
                                            println("Failed to save calendar")
                                            return@thenAccept
                                        }
                                    }

                                }

                            }.exceptionally { throwable ->
                                println(throwable.message)
                                return@exceptionally null
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