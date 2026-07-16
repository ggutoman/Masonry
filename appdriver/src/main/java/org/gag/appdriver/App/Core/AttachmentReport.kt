package org.gag.appdriver.App.Core

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import io.ktor.client.call.body
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.gag.appdriver.App.DataModels.DownloadError
import org.gag.appdriver.App.DataModels.DownloadKey
import org.gag.appdriver.App.DataModels.DownloadLodgeCalendar
import org.gag.appdriver.App.DataModels.DownloadUserInfo
import org.gag.appdriver.App.Models.LodgeCalendarList
import org.gag.appdriver.App.Models.LodgeInfo
import org.gag.appdriver.App.Models.TownProvince
import org.gag.appdriver.Constants.API_CONSTANTS
import org.gag.appdriver.Constants.FILE_CONSTANTS
import org.gag.appdriver.Libraries.DateUtil.DateRepository
import org.gag.appdriver.Libraries.Encryption.HashRepository
import org.gag.appdriver.Libraries.File.FileHelper
import org.gag.appdriver.Libraries.HTTP.KTORepository
import org.gag.appdriver.Libraries.Preferences.AppConfig
import org.gag.appdriver.Libraries.TextLibrary.TextFormatter
import org.gag.appdriver.Room.DataObject.DLodgeCalendar
import org.gag.appdriver.Room.DataObject.DLodgeInfo
import org.gag.appdriver.Room.DataObject.DTownInfo
import org.gag.appdriver.Room.Entities.ELodgeCalendar
import org.gag.appdriver.Room.Entities.ELodgeInfo
import org.gag.appdriver.Room.ML_DBF
import org.json.JSONObject
import java.util.concurrent.CompletableFuture

class AttachmentReport(loInstance : Context) {

    lateinit var message : String

    val loContext : Context = loInstance
    val session : AppConfig = AppConfig(loInstance)
    val httpInstance : KTORepository = KTORepository(loInstance)

    val poFile : FileHelper = FileHelper(loInstance)

    fun GetMessage() : String = message

    @SuppressLint("MissingPermission")
    fun GenerateSOA() : CompletableFuture<Any> {

        val future = CompletableFuture<Any>()
        CoroutineScope(Dispatchers.IO).launch {

            val result = try {

                if (!httpInstance.checkDeviceConnection(loContext)) {
                    message = "No internet connection"
                    false
                }

                val loParams = JSONObject().apply {
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_DOWNLOAD_SOA.fsURL,
                    loParams,
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->

                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData =
                                Json.Default.decodeFromString<DownloadKey>(result.data.body())

                            Log.d("RESULT GENERATE", "${API_CONSTANTS.URL_BASE_SERVER.fsURL}${resultData.GetPayload()}")
                            "${API_CONSTANTS.URL_BASE_SERVER.fsURL}${resultData.GetPayload()}"
                            true
                        }

                        is KTORepository.OnRequest.onFailed -> {
                            val errorData =
                                Json.Default.decodeFromString<DownloadError>(result.data.body())
                            message = errorData.GetPayload().message
                            false
                        }

                        is KTORepository.OnRequest.onError<*> -> {
                            message =  "Could not make request at this moment:\n\n ${result.exception.toString()}"
                            false
                        }

                        else -> {
                            message = "Invalid transaction. Could not proceed"
                            false
                        }
                    }
                }
            } catch (ex: Exception) {
                message =  "Could not make request at this moment:\n\n ${ex.message}"
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