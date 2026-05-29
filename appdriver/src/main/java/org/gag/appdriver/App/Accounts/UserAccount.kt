package org.gag.appdriver.App.Accounts

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import org.gag.appdriver.App.DataModels.DownloadError
import io.ktor.client.call.body
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.gag.appdriver.App.DataModels.DownloadLogin
import org.gag.appdriver.Constants.API_CONSTANTS
import org.gag.appdriver.Libraries.DateUtil.DateRepository
import org.gag.appdriver.Libraries.Encryption.HashRepository
import org.gag.appdriver.Libraries.HTTP.KTORepository
import org.gag.appdriver.Libraries.Preferences.AppConfig
import org.gag.appdriver.Libraries.TextLibrary.TextFormatter
import org.gag.appdriver.Room.DataObject.DMemberInfo
import org.gag.appdriver.Room.DataObject.DUserInfo
import org.gag.appdriver.Room.Entities.EUserInfo
import org.gag.appdriver.Room.ML_DBF
import org.json.JSONObject
import java.util.concurrent.CompletableFuture

class UserAccount(instance : Context) {

    var message : String = "No message found"

    val session : AppConfig = AppConfig(instance)
    val loInstance : Context = instance
    val httpInstance : KTORepository = KTORepository(instance)
    val encryptObj : HashRepository = HashRepository()
    val dateObj : DateRepository = DateRepository()
    val poUserInfo : DUserInfo = ML_DBF.getDatabase(instance).GetUserDao()
    val poMemberInfo : DMemberInfo = ML_DBF.getDatabase(instance).GetMemberDao()

    fun GetMessage() : String = message

    fun GetSession() : AppConfig = session

    fun GetUserInfo() : LiveData<EUserInfo> = poUserInfo.GetUser()

    fun GetEncryption() : HashRepository = encryptObj

    @SuppressLint("MissingPermission")
    fun LoginUser(fsID: String, fsPass: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                } else {
                    val loParams = JSONObject().apply {
                        put("sUserName", fsID)
                    }

                    val lsEncrPass = encryptObj.EncryptHex(fsPass)
                    if (lsEncrPass.isEmpty()) {
                        message = "Could not generate passkey"
                        false
                    } else {
                        loParams.put("sPassword", lsEncrPass)

                        when (val response = httpInstance.makeRequest(
                            API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_LOGIN_ACCOUNT.fsURL,
                            loParams,
                            mapOf()
                        )) {
                            is KTORepository.OnRequest.onSuccess -> {
                                val resultData = Json.decodeFromString<DownloadLogin>(response.data.body())
                                val sTokenIDxx = resultData.GetPayload()

                                session.isLogIn("1")
                                session.setLogDate(dateObj.GetCurrentDate())
                                session.setTokenID(sTokenIDxx)

                                true
                            }

                            is KTORepository.OnRequest.onFailed -> {
                                val errorData = Json.decodeFromString<DownloadError>(response.data.body())
                                message = errorData.GetPayload().message
                                false
                            }

                            is KTORepository.OnRequest.onError<*> -> {
                                message = response.exception.toString()
                                false
                            }

                            else -> {
                                message = "Invalid transaction. Could not proceed"
                                false
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                message = ex.message ?: "Unknown error"
                false
            }

            // Complete the future on the main thread
            withContext(Dispatchers.Main) {
                future.complete(result)
            }
        }

        return future
    }

    @SuppressLint("MissingPermission")
    fun CreateUser(poUser : EUserInfo): CompletableFuture<Boolean> {

        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val result =  try {

                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                }

                val lsEncrPass: String = encryptObj.EncryptHex(poUser.sPassword)
                if (lsEncrPass.isEmpty()) {
                    message = "Could not generate passkey"
                    false
                }
                poUser.sPassword = lsEncrPass

                val loParams = JSONObject().apply {
                    put("sUserName", poUser.sUserName)
                    put("sPassword", poUser.sPassword)
                    put("sGLPIDNoX", poUser.sGLPIDNoX)
                    put("sLastName", poUser.sLastName)
                    put("dBirthDte", poUser.dBirthDte)
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_CREATE_ACCOUNT.fsURL,
                    loParams,
                    mapOf()
                ).let { result ->

                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {
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

            // Complete the future on the main thread
            withContext(Dispatchers.Main) {
                future.complete(result)
            }
        }

        return future

    }

    @SuppressLint("MissingPermission")
    fun UpdateUser(poUser : EUserInfo): CompletableFuture<Boolean> {

        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val result = try {

                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                }

                val lsEncrPass: String = encryptObj.EncryptHex(poUser.sPassword)
                if (lsEncrPass.isEmpty()) {
                    message = "Could not generate passkey"
                    false
                }
                poUser.sPassword = lsEncrPass

                val loParams = JSONObject().apply {
                    put("sPassword", poUser.sPassword)
                    put("sGLPIDNoX", poUser.sGLPIDNoX)
                    put("sLastName", poUser.sLastName)
                    put("dBirthDte", poUser.dBirthDte)
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_UPDATE_USER.fsURL,
                    loParams,
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->

                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            poMemberInfo.GetMemberInfoByUserID(
                                TextFormatter()
                                    .ExtractFromCharacter(encryptObj.DecryptHex(session.getokenID()), ":") //extract token and get user id placed after colon
                                    .getOrNull(1) ?: ""
                            ).let {

                                if (it == null){

                                    message = "Could not update. Membership information not found"
                                    false
                                }

                                //update glp id, and lastname from member name
                                it.sGLPIDNoX = poUser.sGLPIDNoX
                                it.sMemberNm = TextFormatter()
                                    .ReplaceText(it.sMemberNm,
                                        ",",
                                        0,
                                        poUser.sLastName
                                    )

                                //update user info and member info on local data
                                poMemberInfo.SaveMemberInfo(it)
                                poUserInfo.SaveUserInfo(poUser)
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

}