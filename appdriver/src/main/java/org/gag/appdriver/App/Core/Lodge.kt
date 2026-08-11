package org.gag.appdriver.App.Core

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import io.ktor.client.call.body
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.gag.appdriver.App.DataModels.DownloadError
import org.gag.appdriver.App.DataModels.DownloadLodgeCalendar
import org.gag.appdriver.App.DataModels.DownloadLodgeCalendarList
import org.gag.appdriver.App.DataModels.DownloadLodgeInfo
import org.gag.appdriver.App.Models.LodgeCalendarList
import org.gag.appdriver.App.Models.LodgeInfo
import org.gag.appdriver.App.Models.TownProvince
import org.gag.appdriver.Constants.API_CONSTANTS
import org.gag.appdriver.Libraries.DateUtil.DateRepository
import org.gag.appdriver.Libraries.Encryption.HashRepository
import org.gag.appdriver.Libraries.HTTP.KTORepository
import org.gag.appdriver.Libraries.Preferences.AppConfig
import org.gag.appdriver.Libraries.TextLibrary.TextFormatter
import org.gag.appdriver.Room.DataObject.DLodgeCalendar
import org.gag.appdriver.Room.DataObject.DLodgeInfo
import org.gag.appdriver.Room.DataObject.DTownInfo
import org.gag.appdriver.Room.Entities.ELodgeCalendar
import org.gag.appdriver.Room.ML_DBF
import org.json.JSONObject
import java.util.concurrent.CompletableFuture

class Lodge(loInstance : Context) {

    lateinit var message : String

    val loContext : Context = loInstance
    val session : AppConfig = AppConfig(loInstance)
    val httpInstance : KTORepository = KTORepository(loInstance)
    val encryptObj : HashRepository = HashRepository()

    val poLodgeInfo : DLodgeInfo = ML_DBF.getDatabase(loInstance).GetLodge()
    val poLodgeCalendar : DLodgeCalendar = ML_DBF.getDatabase(loInstance)?.GetLodgeCalendar() as DLodgeCalendar
    val poTownInfo : DTownInfo = ML_DBF.getDatabase(loInstance)?.GetTownCity() as DTownInfo
    val dateRepository : DateRepository = DateRepository()

    fun GetLodgeID() : String = TextFormatter()
        .ExtractFromCharacter(encryptObj.DecryptHex(session.getokenID()), ":")
        .getOrNull(0) ?: ""

    fun GetLodgeCalendarInfo( sYearIDx : String) : LiveData<ELodgeCalendar> = poLodgeCalendar.ObserveLodgeCalendarInfo(sYearIDx)

    fun GetLodges() : LiveData<List<LodgeInfo>> = poLodgeInfo.ObserveLodgeList()

    fun GetLodgeCalendarList(fsLodgeIDx : String, fsDateFrom : String, fsDateTo : String) : LiveData<List<LodgeCalendarList>> = poLodgeCalendar.GetLodgeCalendarList(fsLodgeIDx, fsDateFrom, fsDateTo)

    fun SearchTown(fsSearch : String) : LiveData<List<TownProvince>> {
        return poTownInfo.SearchTown("%$fsSearch%")
    }

    fun GetTownInfo(fsSearch : String) : TownProvince {
        return poTownInfo.GetTownInfo(fsSearch)
    }

    fun GetCurrentDate() : String = dateRepository.GetCurrentDate()

    fun GetCurrentDateTime() : String = dateRepository.GetCurrentDateTime()

    fun GetFormattedLongDate(foLongDate : Long) : String = dateRepository.FormatLongDate(foLongDate)

    fun IsDateCompared( fsDate1 : String, fsDate2 : String) : Boolean = dateRepository.IsDateCompared( fsDate1, fsDate2)

    @SuppressLint("MissingPermission")
    fun SaveLodgeCalendar() : CompletableFuture<Any> {

        val future = CompletableFuture<Any>()
        CoroutineScope(Dispatchers.IO).launch {

            val result = try {

                if (!httpInstance.checkDeviceConnection(loContext)) {
                    message = "No internet connection"
                    false
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_CREATE_LODGE_CALENDAR.fsURL,
                    JSONObject(),
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->

                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData =
                                Json.Default.decodeFromString<DownloadLodgeCalendar>(result.data.body())

                            poLodgeCalendar.SaveLodgeCalendar(resultData.GetPayload())
                            resultData.GetPayload().sYearIDxx
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
    fun DownloadLodgeInfo(): CompletableFuture<Boolean> {
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
                                Json.Default.decodeFromString<DownloadLodgeInfo>(result.data.body())

                            resultData.GetPayload().forEach { loItem ->
                                poLodgeInfo.SaveLodge(loItem)
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

    @SuppressLint("MissingPermission")
    fun DownloadLodgeCalendars(dFromxx : String, dToxx : String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val  result = try {

                if (!httpInstance.checkDeviceConnection(loContext)) {
                    message = "No internet connection"
                    false
                }

                val params : JSONObject = JSONObject().also {
                    it.put("dFromxx", dFromxx)
                    it.put("dToxx", dToxx)
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_GET_LODGE_CALENDAR.fsURL,
                    params,
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->
                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData =
                                Json.Default.decodeFromString<DownloadLodgeCalendarList>(result.data.body())

                            resultData.GetPayload().forEach { loItem ->
                                poLodgeCalendar.SaveLodgeCalendar(loItem)
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
}