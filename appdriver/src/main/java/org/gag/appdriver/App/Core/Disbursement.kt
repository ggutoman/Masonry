package org.gag.appdriver.App.Core

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import androidx.lifecycle.LiveData
import io.ktor.client.call.body
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.gag.appdriver.App.DataModels.DownloadDisbursement
import org.gag.appdriver.App.DataModels.DownloadDisbursementList
import org.gag.appdriver.App.DataModels.DownloadError
import org.gag.appdriver.App.DataModels.DownloadKey
import org.gag.appdriver.App.DataModels.DownloadMemberProjects
import org.gag.appdriver.App.DataModels.DownloadProjectDetail
import org.gag.appdriver.App.DataModels.DownloadProjectInfo
import org.gag.appdriver.App.DataModels.DownloadProjects
import org.gag.appdriver.App.DataModels.DownloadReceiptInfo
import org.gag.appdriver.App.DataModels.DownloadReceiptList
import org.gag.appdriver.App.Models.AnnualMembers
import org.gag.appdriver.App.Models.LodgeCalendarList
import org.gag.appdriver.App.Models.MemberPledges
import org.gag.appdriver.App.Models.MemberProjects
import org.gag.appdriver.App.Models.ProjectDetail
import org.gag.appdriver.App.Models.ReceiptInfo
import org.gag.appdriver.App.Models.ReceiptList
import org.gag.appdriver.App.Models.ReceiptMasterList
import org.gag.appdriver.Constants.API_CONSTANTS
import org.gag.appdriver.Constants.FILE_CONSTANTS
import org.gag.appdriver.Libraries.DateUtil.DateRepository
import org.gag.appdriver.Libraries.Encryption.HashRepository
import org.gag.appdriver.Libraries.File.FileHelper
import org.gag.appdriver.Libraries.File.FileHelper.FileResult
import org.gag.appdriver.Libraries.HTTP.KTORepository
import org.gag.appdriver.Libraries.Preferences.AppConfig
import org.gag.appdriver.Libraries.TextLibrary.TextFormatter
import org.gag.appdriver.Room.DataObject.DAnnualDetail
import org.gag.appdriver.Room.DataObject.DDisbursement
import org.gag.appdriver.Room.DataObject.DFundMaster
import org.gag.appdriver.Room.DataObject.DLodgeCalendar
import org.gag.appdriver.Room.DataObject.DMemberInfo
import org.gag.appdriver.Room.DataObject.DProjects
import org.gag.appdriver.Room.DataObject.DReceipt
import org.gag.appdriver.Room.DataObject.DUserInfo
import org.gag.appdriver.Room.Entities.EAnnualDetail
import org.gag.appdriver.Room.Entities.EDisbursement
import org.gag.appdriver.Room.Entities.ELodgeCalendar
import org.gag.appdriver.Room.Entities.EMemberInfo
import org.gag.appdriver.Room.Entities.EProjectDetail
import org.gag.appdriver.Room.Entities.EProjectMaster
import org.gag.appdriver.Room.Entities.EReceiptDetail
import org.gag.appdriver.Room.Entities.EReceiptMaster
import org.gag.appdriver.Room.Entities.EUserInfo
import org.gag.appdriver.Room.ML_DBF
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.Date
import java.util.concurrent.CompletableFuture

class Disbursement(instance : Context) {

    lateinit var message : String

    val loInstance : Context = instance
    val session : AppConfig = AppConfig(instance)
    val httpInstance : KTORepository = KTORepository(instance)
    val dateRepository : DateRepository = DateRepository()
    val encryptObj : HashRepository = HashRepository()
    val fileHelper : FileHelper = FileHelper(instance)
    val poUserInfo : DUserInfo = ML_DBF.getDatabase(instance)?.GetUserDao() as DUserInfo
    val poDisbursement : DDisbursement = ML_DBF.getDatabase(instance)?.GetDisbursement() as DDisbursement

    fun GetUserInfo() : EUserInfo  = poUserInfo.GetUserInfo()

    fun GetUserID() : String = TextFormatter()
        .ExtractFromCharacter(encryptObj.DecryptHex(session.getokenID()), ":")
        .getOrNull(1) ?: ""

    fun GetLodgeID() : String = TextFormatter()
        .ExtractFromCharacter(encryptObj.DecryptHex(session.getokenID()), ":")
        .getOrNull(0) ?: ""

    fun GetCurrentDate() : String = dateRepository.GetCurrentDate()

    fun GetCurrentDateTime() : String = dateRepository.GetCurrentDateTime()

    fun GetFormattedDateTime(fsInput : String, fsFormat : String) : String = dateRepository.FormatDateTime(fsInput, fsFormat)

    fun FormatDateString(fsDate : String, fsFormat : String) : String = dateRepository.FormatDate(fsDate, fsFormat)

    fun FormatLongDate(fsParam : Long) : String = dateRepository.FormatLongDate(fsParam)

    fun GetCountedDate(fnCount : Int, fnDateIndex : Int, fbIsAdd : Boolean) : String = dateRepository.GetCountedDate(fnCount, fnDateIndex, fbIsAdd)

    fun GetDisbursement(fsTransNox : String) : LiveData<EDisbursement> = poDisbursement.GetDisbursement(fsTransNox)

    fun GetDisbursementList(fDfromxx : String, fDtoxx : String) : LiveData<List<EDisbursement>> = poDisbursement.GetDisbursementList(fDfromxx, fDtoxx)

    fun GenerateFileOutput(fileNme : String) : CompletableFuture<File>{

        val future = CompletableFuture<File>()

        CoroutineScope(Dispatchers.IO).launch {

            fileHelper.GenerateFile(FILE_CONSTANTS.EXTERNAL_STORAGE_DCIM_DOCUMENTS.fileDir, fileNme).let {

                // Complete the future on the main thread
                withContext(Dispatchers.Main) {
                    future.complete(it)
                }
            }
        }
        return future
    }

    @SuppressLint("MissingPermission")
    fun SaveDisbursement(foDisbursement: EDisbursement) : CompletableFuture<Boolean>{

        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                } else {

                    val loParams = JSONObject().apply {

                        if (foDisbursement.sTransNox.isNotEmpty()) put("sTransNox", foDisbursement.sTransNox)

                        put("sSourceCD", foDisbursement.sSourceCD)
                        put("sSourceNo", foDisbursement.sSourceNo)
                        put("sPayeeNme", foDisbursement.sPayeeNme)
                        put("sParticlr", foDisbursement.sParticlr)
                        put("sVouchrNo", foDisbursement.sVouchrNo)
                        put("nAmountxx", foDisbursement.nAmountxx)
                        put("cTranStat", foDisbursement.cTranStat)
                    }

                    when (val response = httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_SAVE_DISBURSEMENT.fsURL,
                        loParams,
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    )) {
                        is KTORepository.OnRequest.onSuccess -> {
                            val resultData = Json.Default.decodeFromString<DownloadKey>(response.data.body())

                            //save disbursement with generated key
                            foDisbursement.sTransNox = resultData.GetPayload()
                            poDisbursement.SaveDisbursement(foDisbursement)

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
    fun DownloadDisbursementList(fDfromxx : String, fDtoxx : String, faStatus : List<String>) : CompletableFuture<Boolean>{

        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                } else {

                    val loParams = JSONObject().apply {
                        put("dFromxx", fDfromxx)
                        put("dToxx", fDtoxx)
                        put("status", JSONArray(faStatus))
                    }

                    when (val response = httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_DOWNLOAD_DISBURSEMENT_LIST.fsURL,
                        loParams,
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    )) {
                        is KTORepository.OnRequest.onSuccess -> {
                            val resultData = Json.Default.decodeFromString<DownloadDisbursementList>(response.data.body())

                            //save disbursement list
                            resultData.GetPayload().forEach { loDisbursement -> poDisbursement.SaveDisbursement(loDisbursement) }

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
    fun DownloadDisbursementInfo(fsTransNox : String) : CompletableFuture<Boolean>{

        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                } else {

                    val loParams = JSONObject().apply { put("sTransNox", fsTransNox) }

                    when (val response = httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_DOWNLOAD_DISBURSEMENT_INFO.fsURL,
                        loParams,
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    )) {
                        is KTORepository.OnRequest.onSuccess -> {
                            val resultData = Json.Default.decodeFromString<DownloadDisbursement>(response.data.body())

                            //save disbursement info
                            poDisbursement.SaveDisbursement(resultData.GetPayload())
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
    fun UpdateDisbursementStatus(foDisb : EDisbursement) : CompletableFuture<Boolean>{

        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                } else {

                    val loParams = JSONObject().apply {
                        put("sTransNox", foDisb.sTransNox)
                        put("cTranStat", foDisb.cTranStat)
                    }

                    when (val response = httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_UPDATE_DISBURSEMENT_STATUS.fsURL,
                        loParams,
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    )) {
                        is KTORepository.OnRequest.onSuccess -> {

                            //save disbursement status
                            poDisbursement.SaveDisbursement(foDisb)
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