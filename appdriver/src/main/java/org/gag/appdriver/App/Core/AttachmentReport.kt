package org.gag.appdriver.App.Core

import android.annotation.SuppressLint
import android.content.Context
import io.ktor.client.call.body
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.gag.appdriver.App.DataModels.DownloadError
import org.gag.appdriver.App.DataModels.DownloadKey
import org.gag.appdriver.Constants.API_CONSTANTS
import org.gag.appdriver.Constants.FILE_CONSTANTS
import org.gag.appdriver.Libraries.File.FileHelper
import org.gag.appdriver.Libraries.HTTP.KTORepository
import org.gag.appdriver.Libraries.Preferences.AppConfig
import org.gag.appdriver.Libraries.TextLibrary.TextFormatter
import org.json.JSONObject
import java.io.File
import java.util.concurrent.CompletableFuture

class AttachmentReport(loInstance : Context) {

    lateinit var message : String

    val loContext : Context = loInstance
    val session : AppConfig = AppConfig(loInstance)
    val httpInstance : KTORepository = KTORepository(loInstance)
    val poTextFormatter : TextFormatter = TextFormatter()

    val poFile : FileHelper = FileHelper(loInstance)

    fun GetMessage() : String = message

    @SuppressLint("MissingPermission")
    fun GenerateSOA(fsFilter : String, fsDfromxx : String, fsDtoxx : String) : CompletableFuture<Any> {

        val future = CompletableFuture<Any>()
        CoroutineScope(Dispatchers.IO).launch {

            val result = try {

                if (!httpInstance.checkDeviceConnection(loContext)) {
                    message = "No internet connection"
                    false
                }

                val loParams = JSONObject().apply {
                    put("transaction", fsFilter)
                    put("dFromxx", fsDfromxx)
                    put("dToxx", fsDtoxx)
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

                            //download file
                            val download_link = "${API_CONSTANTS.URL_BASE_SERVER.fsURL}${resultData.GetPayload()}"
                            val file_name = poTextFormatter.ExtractFromCharacter(resultData.GetPayload(), "/").let {

                                it.get(it.size - 1)
                            }

                            poFile.CreateFileFromUrl(FILE_CONSTANTS.EXTERNAL_STORAGE_DCIM_REPORTS.fileDir,file_name, download_link).let {
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

    @SuppressLint("MissingPermission")
    fun GenerateProjBalance(fsCriteria : String, fsSearch : String) : CompletableFuture<Any> {

        val future = CompletableFuture<Any>()
        CoroutineScope(Dispatchers.IO).launch {

            val result = try {

                if (!httpInstance.checkDeviceConnection(loContext)) {
                    message = "No internet connection"
                    false
                }

                val loParams = JSONObject().apply {
                    put("criteria", fsCriteria)
                    put("search", fsSearch)
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_DOWNLOAD_PROJ_BALANCE.fsURL,
                    loParams,
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

                            poFile.CreateFileFromUrl(FILE_CONSTANTS.EXTERNAL_STORAGE_DCIM_REPORTS.fileDir,file_name, download_link).let {
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

    fun GetReportList(): CompletableFuture<List<File>> {

        val future = CompletableFuture<List<File>>()

        val loDir : File = FILE_CONSTANTS.EXTERNAL_STORAGE_DCIM_REPORTS.fileDir

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