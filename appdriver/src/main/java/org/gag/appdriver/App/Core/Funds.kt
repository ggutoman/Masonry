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
import org.gag.appdriver.App.DataModels.DownloadFundList
import org.gag.appdriver.App.DataModels.DownloadKey
import org.gag.appdriver.App.DataModels.DownloadSaveFund
import org.gag.appdriver.App.Models.LodgeCalendarList
import org.gag.appdriver.Constants.API_CONSTANTS
import org.gag.appdriver.Libraries.DateUtil.DateRepository
import org.gag.appdriver.Libraries.Encryption.HashRepository
import org.gag.appdriver.Libraries.HTTP.KTORepository
import org.gag.appdriver.Libraries.Preferences.AppConfig
import org.gag.appdriver.Libraries.TextLibrary.TextFormatter
import org.gag.appdriver.Room.DataObject.DFundTurnover
import org.gag.appdriver.Room.DataObject.DLodgeCalendar
import org.gag.appdriver.Room.DataObject.DUserInfo
import org.gag.appdriver.Room.Entities.EFundTurnOver
import org.gag.appdriver.Room.Entities.EUserInfo
import org.gag.appdriver.Room.ML_DBF
import org.json.JSONObject
import java.time.Year
import java.util.concurrent.CompletableFuture

class Funds(instance : Context) {

    lateinit var message : String

    val loInstance : Context = instance
    val session : AppConfig = AppConfig(instance)
    val httpInstance : KTORepository = KTORepository(instance)
    val dateRepository : DateRepository = DateRepository()
    val encryptObj : HashRepository = HashRepository()

    val poUserInfo : DUserInfo = ML_DBF.getDatabase(instance)?.GetUserDao() as DUserInfo
    val poFundTurnover : DFundTurnover = ML_DBF.getDatabase(instance)?.GetFundTurnOver() as DFundTurnover
    val poLodgeCalendar : DLodgeCalendar = ML_DBF.getDatabase(instance)?.GetLodgeCalendar() as DLodgeCalendar

    fun ObserveLodgeCalendarList(): LiveData<List<LodgeCalendarList>> = poLodgeCalendar.GetLodgeCalendarList(
                                                                                            TextFormatter()
                                                                                                .ExtractFromCharacter(encryptObj.DecryptHex(session.getokenID()), ":")
                                                                                                .getOrNull(0) ?: ""
                                                                                        )
    

    fun GetUserInfo() : EUserInfo  = poUserInfo.GetUserInfo()

    fun ObserveTurnover(fsTransNox : String): LiveData<EFundTurnOver> = poFundTurnover.ObserveTurnover(fsTransNox)

    fun ObserveTurnoverList( fsYearID: String, fsDfrom : String, fsDto : String): LiveData<List<EFundTurnOver>> = poFundTurnover.ObserveTurnoverList(fsYearID, fsDfrom, fsDto)

    fun GetCurrentDate() = dateRepository.GetCurrentDate()

    fun GetCurentDateTime() = dateRepository.GetCurrentDateTime()

    @SuppressLint("MissingPermission")
    fun CreateFundTurnover(foTurnover : EFundTurnOver) : CompletableFuture<Boolean>{

        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                } else {

                    val loParams = JSONObject().apply {
                        put("sYearIDxx", foTurnover.sYearIDxx)
                        put("nAmountxx", foTurnover.nAmountxx)
                        put("sRemarksx", foTurnover.sRemarksx)
                        put("cTranStat", foTurnover.cTranStat)
                    }

                    when (val response = httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_CREATE_FUND_TURNOVER.fsURL,
                        loParams,
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    )) {
                        is KTORepository.OnRequest.onSuccess -> {
                            val resultData = Json.Default.decodeFromString<DownloadSaveFund>(response.data.body())

                            //update new transaction id
                            foTurnover.sTransNox = resultData.GetPayload().transactid
                            foTurnover.nEndBalxx = resultData.GetPayload().endbalance.toString()

                            //save fund turnover
                            poFundTurnover.SaveTurnover(foTurnover)

                            true
                        }

                        is KTORepository.OnRequest.onFailed -> {
                            val errorData = Json.Default.decodeFromString<DownloadError>(response.data.body())
                            message = errorData.GetPayload().message
                            false
                        }

                        is KTORepository.OnRequest.onError<*> -> {
                            message =  "Could not make request at this moment:\n\n ${response.exception.toString()}"
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

            // Complete the future on the main thread
            withContext(Dispatchers.Main) {
                future.complete(result)
            }
        }

        return future
    }

    @SuppressLint("MissingPermission")
    fun UpdateFundTurnover(foTurnover : EFundTurnOver) : CompletableFuture<Boolean>{

        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                } else {

                    val loParams = JSONObject().apply {
                        put("sTransNox", foTurnover.sTransNox)
                        put("sYearIDxx", foTurnover.sYearIDxx)
                        put("nAmountxx", foTurnover.nAmountxx)
                        put("sRemarksx", foTurnover.sRemarksx)
                        put("cTranStat", foTurnover.cTranStat)
                    }

                    when (val response = httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_UPDATE_FUND_TURNOVER.fsURL,
                        loParams,
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    )) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData = Json.Default.decodeFromString<DownloadSaveFund>(response.data.body())

                            //update new transaction id
                            foTurnover.sTransNox = resultData.GetPayload().transactid
                            foTurnover.nEndBalxx = resultData.GetPayload().endbalance.toString()

                            //save fund turnover
                            poFundTurnover.SaveTurnover(foTurnover)

                            true
                        }

                        is KTORepository.OnRequest.onFailed -> {
                            val errorData = Json.Default.decodeFromString<DownloadError>(response.data.body())
                            message = errorData.GetPayload().message
                            false
                        }

                        is KTORepository.OnRequest.onError<*> -> {
                            message =  "Could not make request at this moment:\n\n ${response.exception.toString()}"
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

            // Complete the future on the main thread
            withContext(Dispatchers.Main) {
                future.complete(result)
            }
        }

        return future
    }

    fun ApproveFundTurnover(foTurnover : EFundTurnOver) : CompletableFuture<Boolean>{

        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                } else {

                    val loParams = JSONObject().apply {
                        put("sTransNox", foTurnover.sTransNox)
                        put("cTranStat", foTurnover.cTranStat)
                    }

                    when (val response = httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.RL_APPROVE_FUND_TURNOVER.fsURL,
                        loParams,
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    )) {
                        is KTORepository.OnRequest.onSuccess -> {

                            //save fund turnover
                            poFundTurnover.SaveTurnover(foTurnover)

                            true
                        }

                        is KTORepository.OnRequest.onFailed -> {
                            val errorData = Json.Default.decodeFromString<DownloadError>(response.data.body())
                            message = errorData.GetPayload().message
                            false
                        }

                        is KTORepository.OnRequest.onError<*> -> {
                            message =  "Could not make request at this moment:\n\n ${response.exception.toString()}"
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

            // Complete the future on the main thread
            withContext(Dispatchers.Main) {
                future.complete(result)
            }
        }

        return future
    }

    @SuppressLint("MissingPermission")
    fun DownloadFundHistory(fsYearID : String, fsDfrom : String, fsDto : String) : CompletableFuture<Boolean>{

        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                } else {

                    val loParams = JSONObject().apply {
                        put("sYearIDxx", fsYearID)
                        put("dFromxx", fsDfrom)
                        put("dToxx", fsDto)
                    }

                    when (val response = httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_CREATE_FUND_HISTORY.fsURL,
                        loParams,
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    )) {
                        is KTORepository.OnRequest.onSuccess -> {
                            val resultData = Json.Default.decodeFromString<DownloadFundList>(response.data.body())

                            resultData.GetPayload().forEach { loItem ->
                                poFundTurnover.SaveTurnover(loItem)
                            }

                            true
                        }

                        is KTORepository.OnRequest.onFailed -> {
                            val errorData = Json.Default.decodeFromString<DownloadError>(response.data.body())
                            message = errorData.GetPayload().message
                            false
                        }

                        is KTORepository.OnRequest.onError<*> -> {
                            message =  "Could not make request at this moment:\n\n ${response.exception.toString()}"
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

            // Complete the future on the main thread
            withContext(Dispatchers.Main) {
                future.complete(result)
            }
        }

        return future
    }
}