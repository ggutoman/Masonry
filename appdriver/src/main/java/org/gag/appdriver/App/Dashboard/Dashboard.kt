package org.gag.appdriver.App.Dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import io.ktor.client.call.body
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.gag.appdriver.App.DataModels.DownloadError
import org.gag.appdriver.App.DataModels.DownloadLodgeInfo
import org.gag.appdriver.App.DataModels.DownloadPositionInfo
import org.gag.appdriver.App.DataModels.DownloadTitleInfo
import org.gag.appdriver.App.DataModels.DownloadUserInfo
import org.gag.appdriver.Constants.API_CONSTANTS
import org.gag.appdriver.Constants.MENU_ITEM_CONSTANTS
import org.gag.appdriver.Constants.MENU_PARENT_CONSTANTS
import org.gag.appdriver.Libraries.Encryption.HashRepository
import org.gag.appdriver.Libraries.HTTP.KTORepository
import org.gag.appdriver.Libraries.Preferences.AppConfig
import org.gag.appdriver.Libraries.TextLibrary.TextFormatter
import org.gag.appdriver.Room.DataObject.DLodgeInfo
import org.gag.appdriver.Room.DataObject.DMemberInfo
import org.gag.appdriver.Room.DataObject.DPositionInfo
import org.gag.appdriver.Room.DataObject.DTitleInfo
import org.gag.appdriver.Room.DataObject.DUserInfo
import org.gag.appdriver.Room.Entities.ELodgeInfo
import org.gag.appdriver.Room.Entities.EMemberInfo
import org.gag.appdriver.Room.ML_DBF
import org.json.JSONObject
import java.util.concurrent.CompletableFuture

class Dashboard(loInstance : Context) {

    var message : String = "No message found"

    val session : AppConfig = AppConfig(loInstance)
    val loContext : Context = loInstance
    val httpInstance : KTORepository = KTORepository(loInstance)
    val poEncrypt : HashRepository = HashRepository()
    val poDBUser : DUserInfo = ML_DBF.getDatabase(loInstance)?.GetUserDao() as DUserInfo
    val poDBMember : DMemberInfo = ML_DBF.getDatabase(loInstance)?.GetMemberDao() as DMemberInfo
    val poLodgeInfo : DLodgeInfo = ML_DBF.getDatabase(loInstance)?.GetLodge() as DLodgeInfo
    val poPositionInfo : DPositionInfo = ML_DBF.getDatabase(loInstance)?.GetPosition() as DPositionInfo
    val poTitleInfo : DTitleInfo = ML_DBF.getDatabase(loInstance)?.GetTitle() as DTitleInfo

    fun ObserverMemberInfoByUserID() : LiveData<DMemberInfo.MemberDashboardInfo>{

        return poDBMember.ObserveMemberInfoByUserID(
            TextFormatter()
                .ExtractFromCharacter(poEncrypt.DecryptHex(session.getokenID()), ":") //extract token and get user id placed after colon
                .getOrNull(1) ?: ""
        )
    }

    fun GetLodgeInfo() : ELodgeInfo{

        return poLodgeInfo.GetLodgeInfo(
            TextFormatter()
                .ExtractFromCharacter(poEncrypt.DecryptHex(session.getokenID()), ":") //extract token and get user id placed after colon
                .getOrNull(0) ?: ""
        )
    }

    @SuppressLint("MissingPermission")
    fun DownloadUserInfo(): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val result = try {

                if (!httpInstance.checkDeviceConnection(loContext)) {
                    message = "No internet connection"
                    false
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_DOWNLOAD_USER.fsURL,
                    JSONObject(),
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->
                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData =
                                Json.decodeFromString<DownloadUserInfo>(result.data.body())

                            poDBUser.SaveUserInfo(resultData.GetPayload().user_info)
                            poDBMember.SaveMemberInfo(resultData.GetPayload().member_info)

                            true
                        }

                        is KTORepository.OnRequest.onFailed -> {
                            val errorData =
                                Json.decodeFromString<DownloadError>(result.data.body())
                            message = errorData.GetPayload().message

                            false
                        }

                        is KTORepository.OnRequest.onError<*> -> {
                            message = result.exception.toString()
                            false
                        }

                        else -> {
                            message = "Invalid transaction. Could not proceed"
                            false
                        }
                    }
                }
            } catch (ex: Exception) {
                message = ex.message.toString()
                false
            }

            //Complete the future on the main thread
            withContext(Dispatchers.Main) {
                future.complete(result)
            }


        }
        return future
    }

    @SuppressLint("MissingPermission")
    fun DownloadLodgeInfo(): CompletableFuture<Boolean>{
        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val  result = try {

                if (!httpInstance.checkDeviceConnection(loContext)) {
                    message = "No internet connection"
                    false
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_GET_LODGE.fsURL,
                    JSONObject(),
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->
                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData =
                                Json.decodeFromString<DownloadLodgeInfo>(result.data.body())

                            resultData.GetPayload().forEach { loItem ->
                                poLodgeInfo.SaveLodge(loItem)
                            }

                            true
                        }

                        is KTORepository.OnRequest.onFailed -> {
                            val errorData =
                                Json.decodeFromString<DownloadError>(result.data.body())
                            message = errorData.GetPayload().message

                            false
                        }

                        is KTORepository.OnRequest.onError<*> -> {
                            message = result.exception.toString()
                            false
                        }

                        else -> {
                            message = "Invalid transaction. Could not proceed"
                            false
                        }
                    }
                }
            } catch (ex: Exception) {
                message = ex.message.toString()
                false
            }

            //Complete the future on the main thread
            withContext(Dispatchers.Main) {
                future.complete(result)
            }
        }
        return future
    }

    @SuppressLint("MissingPermission")
    fun DownloadPositionInfo(): CompletableFuture<Boolean>{
        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val  result = try {

                if (!httpInstance.checkDeviceConnection(loContext)) {
                    message = "No internet connection"
                    false
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_GET_POSITION.fsURL,
                    JSONObject(),
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->
                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData =
                                Json.decodeFromString<DownloadPositionInfo>(result.data.body())

                            resultData.GetPayload().forEach { loItem ->
                                poPositionInfo.SavePosition(loItem)
                            }

                            true
                        }

                        is KTORepository.OnRequest.onFailed -> {
                            val errorData =
                                Json.decodeFromString<DownloadError>(result.data.body())
                            message = errorData.GetPayload().message

                            false
                        }

                        is KTORepository.OnRequest.onError<*> -> {
                            message = result.exception.toString()
                            false
                        }

                        else -> {
                            message = "Invalid transaction. Could not proceed"
                            false
                        }
                    }
                }
            } catch (ex: Exception) {
                message = ex.message.toString()
                false
            }

            //Complete the future on the main thread
            withContext(Dispatchers.Main) {
                future.complete(result)
            }
        }
        return future
    }

    @SuppressLint("MissingPermission")
    fun DownloadTitleInfo(): CompletableFuture<Boolean>{
        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val  result = try {

                if (!httpInstance.checkDeviceConnection(loContext)) {
                    message = "No internet connection"
                    false
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_GET_POSITION.fsURL,
                    JSONObject(),
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->
                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData =
                                Json.decodeFromString<DownloadTitleInfo>(result.data.body())

                            resultData.GetPayload().forEach { loItem ->
                                poTitleInfo.SaveTitle(loItem)
                            }

                            true
                        }

                        is KTORepository.OnRequest.onFailed -> {
                            val errorData =
                                Json.decodeFromString<DownloadError>(result.data.body())
                            message = errorData.GetPayload().message

                            false
                        }

                        is KTORepository.OnRequest.onError<*> -> {
                            message = result.exception.toString()
                            false
                        }

                        else -> {
                            message = "Invalid transaction. Could not proceed"
                            false
                        }
                    }
                }
            } catch (ex: Exception) {
                message = ex.message.toString()
                false
            }

            //Complete the future on the main thread
            withContext(Dispatchers.Main) {
                future.complete(result)
            }
        }
        return future
    }

    fun GetParentMenus(fnUserLvl : Int) : List<MENU_PARENT_CONSTANTS>{

        return buildList{

            for (entries in MENU_PARENT_CONSTANTS.entries){

                if (entries.fnActive > 0){

                    if (fnUserLvl >= entries.fnLevel){
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
                        if (fnUserLvl  >= items.fnLevel){

                            add(items)
                        }
                    }
                }
            }
        }
    }
}