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
import org.gag.appdriver.App.DataModels.DownloadFundInfo
import org.gag.appdriver.App.DataModels.DownloadLodgeFundList
import org.gag.appdriver.App.Models.LodgeFundInfo
import org.gag.appdriver.App.Models.LodgeFundMasterList
import org.gag.appdriver.App.Models.LodgeInfo
import org.gag.appdriver.Constants.API_CONSTANTS
import org.gag.appdriver.Libraries.DateUtil.DateRepository
import org.gag.appdriver.Libraries.HTTP.KTORepository
import org.gag.appdriver.Libraries.Preferences.AppConfig
import org.gag.appdriver.Room.DataObject.DFundLedger
import org.gag.appdriver.Room.DataObject.DFundMaster
import org.gag.appdriver.Room.DataObject.DFundTurnover
import org.gag.appdriver.Room.DataObject.DLodgeInfo
import org.gag.appdriver.Room.DataObject.DUserInfo
import org.gag.appdriver.Room.Entities.EFundLedger
import org.gag.appdriver.Room.Entities.ELodgeInfo
import org.gag.appdriver.Room.Entities.EUserInfo
import org.gag.appdriver.Room.ML_DBF
import org.json.JSONObject
import java.util.concurrent.CompletableFuture

class Funds(instance : Context) {

    var message : String = "No message found"

    val loInstance : Context = instance
    val session : AppConfig = AppConfig(instance)
    val httpInstance : KTORepository = KTORepository(instance)
    val dateRepository : DateRepository = DateRepository()

    val poUserInfo : DUserInfo = ML_DBF.getDatabase(instance)?.GetUserDao() as DUserInfo
    val poLodge : DLodgeInfo = ML_DBF.getDatabase(instance)?.GetLodge() as DLodgeInfo
    val poLodgeFundMaster : DFundMaster = ML_DBF.getDatabase(instance)?.GetFundMaster() as DFundMaster
    val poLodgeFundLedger : DFundLedger = ML_DBF.getDatabase(instance)?.GetFundLedger() as DFundLedger
    val poFundTurnover : DFundTurnover = ML_DBF.getDatabase(instance)?.GetFundTurnOver() as DFundTurnover


    fun GetLodgeInfo(fsLodgeIDxx : String) : ELodgeInfo = poLodge.GetLodgeInfo(fsLodgeIDxx)

    fun ObserveLodgeFund(fsLodgeIDxx : String) : LiveData<LodgeFundMasterList> = poLodgeFundMaster.ObserveLodgeFund(fsLodgeIDxx)

    fun ObserveLedgers(fsLodgeIDxx: String, fsDfromx: String, fsDTox: String): LiveData<List<EFundLedger>> = poLodgeFundLedger.ObserveLedgers(fsLodgeIDxx, fsDfromx, fsDTox)

    fun GetUserInfo() : EUserInfo  = poUserInfo.GetUserInfo()

    fun GetCurrentDate() = dateRepository.GetCurrentDate()

    fun GetFormattedDate(fsInput : String, fsFormat : String) : String = dateRepository.FormatDate(fsInput, fsFormat)

    @SuppressLint("MissingPermission")
    fun SaveLodgeFundMaster() : CompletableFuture<Boolean> {

        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val result = try {

                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false

                }else{

                    httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_CREATE_FUND_MASTER.fsURL,
                        JSONObject(),
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    ).let { result ->

                        when (result) {
                            is KTORepository.OnRequest.onSuccess -> {
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
    fun SaveFundTurnover() : CompletableFuture<Boolean> {

        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val result = try {

                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false

                }else{

                    httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_GENERATE_FUND_TURNOVER.fsURL,
                        JSONObject(),
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    ).let { result ->

                        when (result) {
                            is KTORepository.OnRequest.onSuccess -> {
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
    fun DownloadFundMasterLedger(fsDfrom : String, fsDto : String) : CompletableFuture<Boolean>{

        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false

                } else {

                    val loParams = JSONObject().apply {
                        put("dFromxx", fsDfrom)
                        put("dToxx", fsDto)
                    }

                    when (val response = httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_DOWNLOAD_FUND_INFO.fsURL,
                        loParams,
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    )) {
                        is KTORepository.OnRequest.onSuccess -> {
                            val resultData = Json.Default.decodeFromString<DownloadFundInfo>(response.data.body())

                            val lodgeFund : LodgeFundInfo = resultData.GetPayload()

                            poLodgeFundMaster.SaveFundMaster(lodgeFund.master)

                            lodgeFund.ledger.forEach { loItem ->
                                poLodgeFundLedger.SaveFundLedger(loItem)
                            }

                            lodgeFund.turnover.forEach { loItem ->
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

    @SuppressLint("MissingPermission")
    fun DownloadFundMasterList(fsLodgeIDxx : String) : CompletableFuture<Boolean>{

        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false

                } else {

                    val loParams = JSONObject().apply {
                        put("sLodgeIDx", fsLodgeIDxx)
                    }

                    when (val response = httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_DOWNLOAD_LODGE_FUNDS.fsURL,
                        loParams,
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    )) {
                        is KTORepository.OnRequest.onSuccess -> {
                            val resultData = Json.Default.decodeFromString<DownloadLodgeFundList>(response.data.body())

                            resultData.GetPayload().forEach { loItem -> poLodgeFundMaster.SaveFundMaster(loItem) }
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