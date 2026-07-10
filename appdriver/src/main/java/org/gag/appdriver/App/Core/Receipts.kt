package org.gag.appdriver.App.Core

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import io.ktor.client.call.body
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
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
import org.gag.appdriver.Libraries.DateUtil.DateRepository
import org.gag.appdriver.Libraries.Encryption.HashRepository
import org.gag.appdriver.Libraries.File.FileHelper
import org.gag.appdriver.Libraries.File.FileHelper.FileResult
import org.gag.appdriver.Libraries.HTTP.KTORepository
import org.gag.appdriver.Libraries.Preferences.AppConfig
import org.gag.appdriver.Libraries.TextLibrary.TextFormatter
import org.gag.appdriver.Room.DataObject.DAnnualDetail
import org.gag.appdriver.Room.DataObject.DLodgeCalendar
import org.gag.appdriver.Room.DataObject.DMemberInfo
import org.gag.appdriver.Room.DataObject.DProjects
import org.gag.appdriver.Room.DataObject.DReceipt
import org.gag.appdriver.Room.DataObject.DUserInfo
import org.gag.appdriver.Room.Entities.EAnnualDetail
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

class Receipts(instance : Context) {

    lateinit var message : String

    val loInstance : Context = instance
    val session : AppConfig = AppConfig(instance)
    val httpInstance : KTORepository = KTORepository(instance)
    val dateRepository : DateRepository = DateRepository()
    val encryptObj : HashRepository = HashRepository()
    val fileHelper : FileHelper = FileHelper(instance)

    val poLodgeYear : DLodgeCalendar = ML_DBF.getDatabase(instance)?.GetLodgeCalendar() as DLodgeCalendar
    val poUserInfo : DUserInfo = ML_DBF.getDatabase(instance)?.GetUserDao() as DUserInfo
    val poReceipt : DReceipt = ML_DBF.getDatabase(instance)?.GetReceipt() as DReceipt
    val poAnnual : DAnnualDetail = ML_DBF.getDatabase(instance)?.GetAnnualDetail() as DAnnualDetail
    val poProjects : DProjects = ML_DBF.getDatabase(instance)?.GetProject() as DProjects
    val poMemberInfo : DMemberInfo = ML_DBF.getDatabase(instance)?.GetMemberDao() as DMemberInfo

    fun GetUserInfo() : EUserInfo  = poUserInfo.GetUserInfo()

    fun GetUserID() : String = TextFormatter()
        .ExtractFromCharacter(encryptObj.DecryptHex(session.getokenID()), ":")
        .getOrNull(1) ?: ""

    fun GetCurrentDate() : String = dateRepository.GetCurrentDate()

    fun GetCurrentDateTime() : String = dateRepository.GetCurrentDateTime()

    fun FormatDateString(fsDate : String, fsFormat : String) : String = dateRepository.FormatDate(fsDate, fsFormat)

    fun FormatLongDate(fsParam : Long) : String = dateRepository.FormatLongDate(fsParam)

    fun GetCountedDate(fnCount : Int, fnDateIndex : Int, fbIsAdd : Boolean) : String = dateRepository.GetCountedDate(fnCount, fnDateIndex, fbIsAdd)

    fun GetLodgeCalendars(fsLodgeIDxx : String) : LiveData<List<LodgeCalendarList>> = poLodgeYear.GetLodgeCalendarList(fsLodgeIDxx)

    fun GetMemberInfo(fsMemberIdxx : String) : EMemberInfo = poMemberInfo.GetMemberInfo(fsMemberIdxx)

    fun ObserveReceiptMaster(fsTransnox : String) : LiveData<EReceiptMaster> = poReceipt.ObserveReceiptMaster(fsTransnox)

    fun ObserveReceiptDetail(fsTransnox: String) : LiveData<List<EReceiptDetail>> = poReceipt.ObserveReceiptDetail(fsTransnox)

    fun ObserveAnnualReceipts(fsMemberIDxx: String) : LiveData<List<AnnualMembers>> = poAnnual.GetAnnualMemberDetail(fsMemberIDxx)

    fun ObserveProjectReceipts(fsMemberIDxx: String) : LiveData<List<MemberPledges>> = poProjects.GetMemberPledges(fsMemberIDxx)

    fun ObserveReceiptMasterList(fDfrom : String, fDto : String) : LiveData<List<ReceiptMasterList>> = poReceipt.ObserveReceiptMasterList(fDfrom, fDto)

    fun CreateFileFromView(layoutView : View, fileNme : String) : CompletableFuture<FileResult>{

        val future = CompletableFuture<FileResult>()

        CoroutineScope(Dispatchers.IO).launch {

            val result = try {

                fileHelper.CreateFileFromView(layoutView, fileNme).let {

                    if (!it.isSuccess){
                        message = fileHelper.GetMessage()
                    }

                    //return the file after initializing message
                    it
                }
            } catch (ex: Exception) {
                message =  ex.message.toString()

                FileResult(false, File(""))
            }

            // Complete the future on the main thread
            withContext(Dispatchers.Main) {
                future.complete(result)
            }
        }
        return future
    }

    @SuppressLint("MissingPermission")
    fun DownloadReceipt(fsTransNox : String) : CompletableFuture<Boolean>{

        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                } else {

                    //initialize master parameters
                    val loParams = JSONObject().apply { put("sTransNox", fsTransNox) }

                    when (val response = httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_DOWNLOAD_RECEIPT.fsURL,
                        loParams,
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    )) {
                        is KTORepository.OnRequest.onSuccess -> {
                            val resultData = Json.Default.decodeFromString<DownloadReceiptInfo>(response.data.body())

                            //save master with generated key
                            val loReceipt : ReceiptInfo = resultData.GetPayload()

                            poReceipt.SaveReceiptMaster(loReceipt.master)

                            //clear detail first to get the accurate data from server
                            poReceipt.DeleteReceiptDetails(loReceipt.master.sTransNox)

                            //clear and save detail
                            loReceipt.detail.forEach { loItem ->
                                poReceipt.SaveReceiptDetail(loItem)
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
    fun DownloadReceiptList(fDfromxx : String, fDToxx : String, faTranStat : List<String>) : CompletableFuture<Boolean>{

        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                } else {

                    //initialize master parameters
                    val loParams = JSONObject().apply {
                        put("dFromxx", fDfromxx)
                        put("dToxx", fDToxx)
                        put("status", JSONArray(faTranStat))
                    }

                    when (val response = httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_DOWNLOAD_RECEIPT_LIST.fsURL,
                        loParams,
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    )) {
                        is KTORepository.OnRequest.onSuccess -> {
                            val resultData = Json.Default.decodeFromString<DownloadReceiptList>(response.data.body())

                            //save master with generated key
                            val loReceipt : ReceiptList = resultData.GetPayload()

                            loReceipt.master.forEach { loitem->

                                poReceipt.SaveReceiptMaster(loitem)

                                //clear detail first to get the accurate data from server
                                poReceipt.DeleteReceiptDetails(loitem.sTransNox)
                            }

                            //clear and save detail
                            loReceipt.detail.forEach { loItem -> poReceipt.SaveReceiptDetail(loItem) }
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
    fun SaveReceipt(loMaster : EReceiptMaster, loDetail : List<EReceiptDetail>) : CompletableFuture<Boolean>{

        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                } else {

                    //initialize array of parameters for details
                    val loDetailParams = JSONArray()

                    loDetail.forEach { loItem ->
                        val obj = JSONObject().apply {

                            //add transaction no to parameters, if not empty
                            if (loItem.sTransNox.isNotEmpty()) put("sTransNox", loItem.sTransNox)

                            put("sSourceCD", loItem.sSourceCD)
                            put("sSourceNo", loItem.sSourceNo)
                            put("nAmtPaidx", loItem.nAmtPaidx)
                            put("dModified", loItem.dModified)
                            put("dTimeStmp", loItem.dTimeStmp)

                        }
                        loDetailParams.put(obj)
                    }

                    //initialize master parameters
                    val loParams = JSONObject().apply {

                        //add transaction no to parameters, if not empty
                        if (loMaster.sTransNox.isNotEmpty()) put("sTransNox", loMaster.sTransNox)

                        put("sMemberID", loMaster.sMemberID)
                        put("cPaymForm", loMaster.cPaymForm)
                        put("sORNoxxxx", loMaster.sORNoxxxx)
                        put("sRemarksx", loMaster.sRemarksx)
                        put("cTranStat", loMaster.cTranStat)
                        put("detail", loDetailParams)
                    }

                    when (val response = httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_SAVE_RECEIPT.fsURL,
                        loParams,
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    )) {
                        is KTORepository.OnRequest.onSuccess -> {
                            val resultData = Json.Default.decodeFromString<DownloadKey>(response.data.body())

                            //save master with generated key
                            loMaster.sTransNox = resultData.GetPayload()
                            poReceipt.SaveReceiptMaster(loMaster)

                            //clear and save detail
                            if(loMaster.sTransNox.isNotEmpty()) { poReceipt.DeleteReceiptDetails(loMaster.sTransNox) }
                            loDetail.forEach { loItem ->
                                loItem.sTransNox = loMaster.sTransNox
                                poReceipt.SaveReceiptDetail(loItem)
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
    fun UpdateReceiptStatus(loReceipt : EReceiptMaster) : CompletableFuture<Boolean>{

        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                } else {

                    //initialize master parameters
                    val loParams = JSONObject().apply {

                        put("sTransNox", loReceipt.sTransNox)
                        put("cTranStat", loReceipt.cTranStat)
                        put("cSendStat", loReceipt.cSendStat)
                    }

                    when (val response = httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_UPDATE_RECEIPT_STATUS.fsURL,
                        loParams,
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    )) {
                        is KTORepository.OnRequest.onSuccess -> {

                            //save master
                            poReceipt.SaveReceiptMaster(loReceipt)

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