package org.gag.appdriver.App.Accounts

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import org.gag.appdriver.App.DataModels.DownloadError
import io.ktor.client.call.body
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.gag.appdriver.App.DataModels.DownloadLogin
import org.gag.appdriver.Constants.API_CONSTANTS
import org.gag.appdriver.Libraries.DateUtil.DateRepository
import org.gag.appdriver.Libraries.Encryption.HashRepository
import org.gag.appdriver.Libraries.HTTP.KTORepository
import org.gag.appdriver.Libraries.Preferences.AppConfig
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
    val poDBUser : DUserInfo = ML_DBF.getDatabase(instance).GetUserDao()

    fun GetMessage() : String = message

    @SuppressLint("MissingPermission")
    fun LoginUser(fsID: String, fsPass: String): CompletableFuture<Boolean> {
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
                    put("sUserName", fsID)
                }

                val lsEncrPass: String = encryptObj.EncryptHex(fsPass)
                if (lsEncrPass.isEmpty()) {
                    message = "Could not generate passkey"
                    Handler(Looper.getMainLooper()).post {
                        future.complete(false)
                    }
                    return@launch
                }
                loParams.put("sPassword", lsEncrPass)

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_LOGIN_ACCOUNT.fsURL,
                    loParams,
                    mapOf()
                ).let { result ->
                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData =
                                Json.decodeFromString<DownloadLogin>(result.data.body())

                            val sTokenIDxx = resultData.GetPayload();

                            session.isLogIn("1")
                            session.setLogDate(dateObj.GetCurrentDate())
                            session.setTokenID(sTokenIDxx)

                            Handler(Looper.getMainLooper()).post {
                                future.complete(true)
                            }
                        }

                        is KTORepository.OnRequest.onFailed -> {
                            val errorData =
                                Json.decodeFromString<DownloadError>(result.data.body())
                            message = errorData.GetPayload().message

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

    @SuppressLint("MissingPermission")
    fun CreateUser(poUser : EUserInfo): CompletableFuture<Boolean> {
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

                val lsEncrPass: String = encryptObj.EncryptHex(poUser.sPassword)
                if (lsEncrPass.isEmpty()) {
                    message = "Could not generate passkey"
                    Handler(Looper.getMainLooper()).post {
                        future.complete(false)
                    }
                    return@launch
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

                            Handler(Looper.getMainLooper()).post {
                                future.complete(true)
                            }
                        }

                        is KTORepository.OnRequest.onFailed -> {
                            val errorData =
                                Json.decodeFromString<DownloadError>(result.data.body())
                            message = errorData.GetPayload().message

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

    @SuppressLint("MissingPermission")
    fun UpdateCredentials(poUser : EUserInfo): CompletableFuture<Boolean> {
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

                val lsEncrPass: String = encryptObj.EncryptHex(poUser.sPassword)
                if (lsEncrPass.isEmpty()) {
                    message = "Could not generate passkey"
                    Handler(Looper.getMainLooper()).post {
                        future.complete(false)
                    }
                    return@launch
                }
                poUser.sPassword = lsEncrPass

                val loParams = JSONObject().apply {
                    put("sPassword", poUser.sPassword)
                    put("sGLPIDNoX", poUser.sGLPIDNoX)
                    put("sLastName", poUser.sLastName)
                    put("dBirthDte", poUser.dBirthDte)
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_UPDATE_ACCOUNT.fsURL,
                    loParams,
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->

                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            poUser.dModified = dateObj.GetCurrentDateTime()
                            poDBUser.SaveUserInfo(poUser)

                            Handler(Looper.getMainLooper()).post {
                                future.complete(true)
                            }
                        }

                        is KTORepository.OnRequest.onFailed -> {
                            val errorData =
                                Json.decodeFromString<DownloadError>(result.data.body())
                            message = errorData.GetPayload().message

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

}