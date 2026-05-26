package org.gag.appdriver.App.Dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import io.ktor.client.call.body
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.gag.appdriver.App.DataModels.DownloadError
import org.gag.appdriver.App.DataModels.DownloadUserInfo
import org.gag.appdriver.Constants.API_CONSTANTS
import org.gag.appdriver.Constants.MENU_ITEM_CONSTANTS
import org.gag.appdriver.Constants.MENU_PARENT_CONSTANTS
import org.gag.appdriver.Libraries.HTTP.KTORepository
import org.gag.appdriver.Libraries.Preferences.AppConfig
import org.gag.appdriver.Room.DataObject.DMemberInfo
import org.gag.appdriver.Room.DataObject.DUserInfo
import org.gag.appdriver.Room.ML_DBF
import org.json.JSONObject
import java.util.concurrent.CompletableFuture

class Dashboard(loInstance : Context) {

    var message : String = "No message found"

    val session : AppConfig = AppConfig(loInstance)
    val loInstance : Context = loInstance
    val httpInstance : KTORepository = KTORepository(loInstance)
    val poDBUser : DUserInfo = ML_DBF.getDatabase(loInstance)?.GetUserDao() as DUserInfo
    val poDBMember : DMemberInfo = ML_DBF.getDatabase(loInstance)?.GetMemberDao() as DMemberInfo

    val poDB = ML_DBF.getDatabase(loInstance)

    @SuppressLint("MissingPermission")
    fun DownloadUserInfo(fsID: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            if (!httpInstance.checkDeviceConnection(loInstance)) {
                message = "No internet connection"
                Handler(Looper.getMainLooper()).post {
                    future.complete(false)
                }
                return@launch
            }

            try {
                val loParams = JSONObject().apply {
                    put("sUserIDxx", fsID)
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_DOWNLOAD_USER.fsURL,
                    loParams
                ).let { result ->
                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData =
                                Json.decodeFromString<DownloadUserInfo>(result.data.body())

                            poDBUser.SaveUserInfo(resultData.GetPayload().userIinfo)
                            poDBMember.SaveMemberInfo(resultData.GetPayload().memberInfo)

                            Handler(Looper.getMainLooper()).post {
                                future.complete(true)
                            }
                        }

                        is KTORepository.OnRequest.onFailed -> {
                            val errorData =
                                Json.decodeFromString<DownloadError>(result.data.body())
                            message = errorData.GetError().message

                            Handler(Looper.getMainLooper()).post {
                                future.complete(false)
                            }
                        }

                        is KTORepository.OnRequest.onError<*> -> {
                            message = result.exception.toString()
                            Handler(Looper.getMainLooper()).post {
                                future.complete(false)
                            }
                        }

                        else -> {
                            message = "Invalid transaction. Could not proceed"
                            Handler(Looper.getMainLooper()).post {
                                future.complete(false)
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                message = ex.message.toString()
                Handler(Looper.getMainLooper()).post {
                    future.complete(false)
                }
            }
        }
        return future
    }

    fun GetUserInfo() = poDB

    fun GetParentMenus(fnUserLvl : Int) : List<MENU_PARENT_CONSTANTS>{

        return buildList{

            for (entries in MENU_PARENT_CONSTANTS.entries){

                if (entries.fnActive > 0){

                    if (fnUserLvl >= 0){
                        add(entries)
                    }
                }
            }

        }
    }

    fun GetParentItems(fnUserLvl : Int, fsParentIDx : String) : List<MENU_ITEM_CONSTANTS>{

        return buildList {

            for (items in MENU_ITEM_CONSTANTS.entries){

                if (items.fnActive > 0){

                    if (fsParentIDx.equals(items.fsParentIDx)){
                        if (fnUserLvl >= 0){
                            add(items)
                        }
                    }
                }
            }
        }
    }
}