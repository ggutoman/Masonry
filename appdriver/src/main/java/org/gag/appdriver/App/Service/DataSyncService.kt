package org.gag.appdriver.App.Service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.gag.appdriver.App.Core.Annual
import org.gag.appdriver.App.Core.Dashboard
import org.gag.appdriver.App.Core.Funds
import org.gag.appdriver.App.Core.Lodge
import org.gag.appdriver.App.Core.Projects
import org.gag.appdriver.App.Core.UserAccount
import org.gag.appdriver.Constants.DATE_CONSTANTS
import org.gag.appdriver.Libraries.DateUtil.DateRepository
import org.gag.appdriver.Room.Entities.EProjectMaster
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

                            stopSelf(startId)
                            return@launch
                        }

                        val userId = loIntent.getStringExtra("user_id")
                        val memberId = loIntent.getStringExtra("member_id")

                        if (!userId.isNullOrEmpty()) {

                            Lodge(applicationContext).apply {

                                //initialize core classes
                                val poDashboard = Dashboard(applicationContext)
                                val poAccount = UserAccount(applicationContext)
                                val poAnnual = Annual(applicationContext)
                                val poProject = Projects(applicationContext)
                                val poFunds = Funds(applicationContext)

                                val poDate = DateRepository()

                                //download lodge information
                                DownloadLodgeInfo().thenAccept { lodgeSuccess ->

                                    //continue if lodge information is downloaded
                                    if (lodgeSuccess) {

                                        //save lodge fund and masonic calendar
                                        val laSaveData = listOf(
                                            poFunds.SaveLodgeFundMaster(),
                                            SaveLodgeCalendar()
                                        )

                                        CompletableFuture.allOf(*laSaveData.toTypedArray()).thenAccept {

                                            laSaveData.forEachIndexed { idx, future ->

                                                val result = future.get()
                                                if (result is Boolean){

                                                    if (!result){
                                                        println("Save $idx Error: ${if (idx == 0) poFunds.message else message}")

                                                        stopSelf(startId)
                                                        return@thenAccept
                                                    }else{

                                                        //current date
                                                        val current_year = poDate.FormatDate(poDate.GetCurrentDate(), "yyyy")

                                                        val start_date = poDate.FormatDate(current_year + "-01-01",DATE_CONSTANTS.DATE_FORMAT.fsDescript)
                                                        val end_date = poDate.FormatDate(current_year + "-12-31", DATE_CONSTANTS.DATE_FORMAT.fsDescript)

                                                        //sync other data
                                                        val laSyncData = listOf(
                                                            poFunds.SaveFundTurnover(),
                                                            poDashboard.DownloadPositionInfo(),
                                                            poDashboard.DownloadProvinceInfo(),
                                                            poDashboard.DownloadTownInfo(),
                                                            poAccount.DownloadUserObjects(),
                                                            poAccount.DownloadUserNotifs(),
                                                            poAccount.CreateReminders(),
                                                            poAccount.DownloadMemberList(start_date, end_date),
                                                            poAccount.DownloadOfficerList(poDate.GetCurrentDate())
                                                        )

                                                        //execute task list
                                                        CompletableFuture.allOf(*laSyncData.toTypedArray()).thenAccept {

                                                            //print each indexed error
                                                            laSyncData.forEachIndexed { idx, future ->

                                                                val result = future.get()
                                                                if (!result) {
                                                                    val errorMsg = when (idx) {
                                                                        0 -> poFunds.message
                                                                        1 -> poAnnual.message
                                                                        2 -> poDashboard.message
                                                                        else -> poAccount.message
                                                                    }.takeIf { it.isNotEmpty() } ?: "No message returned"

                                                                    println("Sync $idx Error $errorMsg")
                                                                } else {
                                                                    println("Sync Data: Task $idx success")
                                                                }
                                                            }

                                                        }.exceptionally { throwable ->
                                                            throwable.printStackTrace()
                                                            stopSelf(startId)

                                                            null
                                                        }

                                                    }
                                                }else if (result is String){

                                                    if (result.isEmpty()){
                                                        println("Save $idx Error: ${if (idx == 0) poFunds.message else message}")

                                                        stopSelf(startId)
                                                        return@thenAccept
                                                    }else{

                                                        val laSaveData = listOf(
                                                            poAnnual.SaveAnnualDue(result, memberId.toString()),
                                                            poProject.DownloadProjectMembers(memberId.toString())
                                                        )

                                                        //execute task list
                                                        CompletableFuture.allOf(*laSaveData.toTypedArray()).thenAccept {

                                                            //print each indexed error
                                                            laSaveData.forEachIndexed { idx, future ->

                                                                val result = future.get()
                                                                if (result is Boolean){

                                                                    if (!result) {
                                                                        val errorMsg = when (idx) {
                                                                            0 -> poAnnual.message
                                                                            1 -> poProject.message
                                                                            else -> "No message found"
                                                                        }.takeIf { it.isNotEmpty() } ?: "No message returned"

                                                                        println("Save $idx Error $errorMsg")
                                                                    }else{
                                                                        println("Save $idx success")
                                                                    }

                                                                }else if (result is List<*>){

                                                                    if (result.size > 0){

                                                                        result.forEach {  project->

                                                                            val item : EProjectMaster = project as EProjectMaster
                                                                            val target_date = poDate.ConvertStringDate(item.dDueDatex,DATE_CONSTANTS.DATE_FORMAT.fsDescript)
                                                                            val current_date = poDate.ConvertStringDate(poDate.GetCurrentDate(),DATE_CONSTANTS.DATE_FORMAT.fsDescript)

                                                                            //update project status as completed, if target date is today and current status is on going
                                                                            if (item.cTranStat == "1" && target_date?.after(current_date) == true){

                                                                                item.cTranStat = "2"
                                                                                poProject.UpdateProjectStatus(item)
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                        }.exceptionally { throwable ->
                                                            throwable.printStackTrace()
                                                            stopSelf(startId)

                                                            null
                                                        }

                                                        //save annual due
                                                        poAnnual.SaveAnnualDue(result, memberId.toString()).thenAccept { isSuccess->

                                                            if (!isSuccess){
                                                                println("Save $idx Error: ${if (idx == 0) poFunds.message else message}")

                                                                stopSelf(startId)
                                                                return@thenAccept
                                                            }
                                                        }.exceptionally { throwable ->
                                                            throwable.printStackTrace()

                                                            stopSelf(startId)
                                                            null
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    }else{
                                        stopSelf(startId)
                                    }

                                }.exceptionally { throwable ->
                                    throwable.printStackTrace()
                                    stopSelf(startId)
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