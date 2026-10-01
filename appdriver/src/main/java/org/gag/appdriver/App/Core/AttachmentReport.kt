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
import org.gag.appdriver.App.DataModels.DownloadReportObjects
import org.gag.appdriver.Constants.API_CONSTANTS
import org.gag.appdriver.Constants.FILE_CONSTANTS
import org.gag.appdriver.Libraries.DateUtil.DateRepository
import org.gag.appdriver.Libraries.Encryption.HashRepository
import org.gag.appdriver.Libraries.File.FileHelper
import org.gag.appdriver.Libraries.HTTP.KTORepository
import org.gag.appdriver.Libraries.Preferences.AppConfig
import org.gag.appdriver.Libraries.TextLibrary.TextFormatter
import org.gag.appdriver.Room.DataObject.DObjectPermission
import org.gag.appdriver.Room.DataObject.DSysReports
import org.gag.appdriver.Room.Entities.EReports
import org.gag.appdriver.Room.ML_DBF
import org.json.JSONObject
import java.io.File
import java.util.concurrent.CompletableFuture

class AttachmentReport(loInstance : Context) {

    lateinit var message : String

    val loContext : Context = loInstance
    val session : AppConfig = AppConfig(loInstance)
    val encryptObj : HashRepository = HashRepository()
    val httpInstance : KTORepository = KTORepository(loInstance)
    val poDate : DateRepository = DateRepository()

    val poTextFormatter : TextFormatter = TextFormatter()
    val poFile : FileHelper = FileHelper(loInstance)

    val poReports : DSysReports = ML_DBF.getDatabase(loInstance).GetReportObjects() as DSysReports
    val poPermissions : DObjectPermission = ML_DBF.getDatabase(loInstance).GetPermissions() as DObjectPermission

    fun GetMessage() : String = message

    fun GetUserID() : String = TextFormatter()
        .ExtractFromCharacter(encryptObj.DecryptHex(session.getokenID()), ":")
        .getOrNull(1) ?: ""

    fun GetReportInfo(fsIDxx : String) : LiveData<EReports>{
        return poReports.GetReportInfo(fsIDxx)
    }

    @SuppressLint("MissingPermission")
    fun GenerateReport(fsIDxx : String, fsTemplateDir : String, foParam : JSONObject) : CompletableFuture<Any> {

        val future = CompletableFuture<Any>()
        CoroutineScope(Dispatchers.IO).launch {

            val result = try {

                if (!httpInstance.checkDeviceConnection(loContext)) {
                    message = "No internet connection"
                    false
                }

                httpInstance.makeRequest(
                    "${API_CONSTANTS.URL_BASE_SERVER.fsURL}$fsTemplateDir",
                    foParam,
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->

                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData =
                                Json.Default.decodeFromString<DownloadKey>(result.data.body())

                            //download file
                            val download_link = "${API_CONSTANTS.URL_BASE_SERVER.fsURL}${resultData.GetPayload()}"
                            val file_name = poTextFormatter.ExtractFromCharacter(resultData.GetPayload(), "/").let {

                                it.get(it.size - 1)
                            }

                            val fileParent = File(FILE_CONSTANTS.EXTERNAL_STORAGE_DCIM_REPORTS.fileDir.absolutePath, "${fsIDxx}/")
                            poFile.CreateFileFromUrl(fileParent, file_name, download_link).let {
                                it.absolutePath
                            }
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

    fun GetObjects() : LiveData<List<EReports>>{
        return poReports.GetReports(GetUserID(), poDate.FormatDate(poDate.GetCurrentDate(), "yyyy"))
    }

    @SuppressLint("MissingPermission")
    fun DownloadReportObjects() : CompletableFuture<Boolean> {

        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val result = try {

                if (!httpInstance.checkDeviceConnection(loContext)) {
                    message = "No internet connection"
                    false
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_DOWNLOAD_USER_REPORTS.fsURL,
                    JSONObject(),
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->

                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData =
                                Json.Default.decodeFromString<DownloadReportObjects>(result.data.body())

                            resultData.GetPayload().attachment.forEach { loItem ->
                                poReports.SaveReport(loItem)
                            }

                            resultData.GetPayload().permission.forEach { loItem ->
                                poPermissions.SavePermission(loItem)
                            }
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

    fun GetReportList(fsIDxx : String): CompletableFuture<List<File>> {

        val future = CompletableFuture<List<File>>()

        val loDir = File(FILE_CONSTANTS.EXTERNAL_STORAGE_DCIM_REPORTS.fileDir.absolutePath, "${fsIDxx}/")

        CoroutineScope(Dispatchers.IO).launch {

            val result: List<File> = if (loDir.exists() && loDir.isDirectory) {
                loDir.listFiles()?.toList() ?: emptyList()
            } else {
                emptyList()
            }

            // Complete the future on the main thread
            withContext(Dispatchers.Main) {
                future.complete(result)
            }
        }
        return future
    }

}