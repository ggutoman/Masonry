package org.gag.appdriver.App.Core

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture

class DataSyncService : Service(){

    companion object {
        var isRunning = false
    }

    override fun onDestroy() {
        super.onDestroy()

        isRunning = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (!isRunning){

            isRunning = true

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

                                //initalize other core classes for syncing data
                                val poAccount = UserAccount(applicationContext)
                                val poAnnual = Annual(applicationContext)
                                val poFunds = Funds(applicationContext)

                                DownloadLodgeInfo().thenAccept { lodgeSuccess ->

                                    //continue syncing data if lodge information is successfully downloaded
                                    if (lodgeSuccess) {

                                        //save lodge calendar
                                        SaveLodgeCalendar().thenAccept { loCalendar->

                                            if (loCalendar is Boolean){

                                                if (!loCalendar){
                                                    println("Save Calendar Error $message")
                                                    return@thenAccept
                                                }
                                            }else if (loCalendar is String){

                                                if (loCalendar.isEmpty()){
                                                    println("Save Calendar Error $message")
                                                    return@thenAccept
                                                }

                                                val laSyncData = hashSetOf(
                                                    poFunds.SaveLodgeFund(),
                                                    poAnnual.SaveAnnualDue(loCalendar, memberId.toString()),
                                                    poAccount.DownloadUserObjects(),
                                                    poAccount.DownloadMemberList(),
                                                    poAccount.DownloadOfficerList()
                                                )

                                                CompletableFuture.allOf(*laSyncData.toTypedArray())
                                                    .thenAccept {
                                                        laSyncData.forEachIndexed { idx, future ->

                                                            val result = future.get()
                                                            if (!result) {
                                                                val errorMsg = when (idx) {
                                                                    0 -> poFunds.message
                                                                    1 -> poAnnual.message
                                                                    else -> poAccount.message
                                                                }.takeIf { it.isNotEmpty() } ?: "No message returned"

                                                                println("Sync Error $errorMsg")
                                                            } else {
                                                                println("Sync Data: Task $idx success")
                                                            }
                                                        }
                                                        // Stop service only after all tasks complete
                                                        stopSelf(startId)
                                                    }
                                                    .exceptionally { throwable ->
                                                        throwable.printStackTrace()
                                                        stopSelf(startId)
                                                        null
                                                    }

                                                // Stop service only after all tasks are done
                                                stopSelf(startId)


                                            }else{
                                                println("Failed to save calendar")
                                                return@thenAccept
                                            }

                                        }
                                    }

                                }.exceptionally { throwable ->
                                    throwable.printStackTrace()
                                    return@exceptionally null
                                }
                            }
                        }
                    }
                }
            }
        }else{
            println("Service has already started")
        }
        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}