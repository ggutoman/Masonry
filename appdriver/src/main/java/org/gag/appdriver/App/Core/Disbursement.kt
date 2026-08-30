package org.gag.appdriver.App.Core

import android.annotation.SuppressLint
import android.content.Context
import android.util.Base64
import androidx.lifecycle.LiveData
import io.ktor.client.call.body
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.gag.appdriver.App.DataModels.DownloadAttachmentDV
import org.gag.appdriver.App.DataModels.DownloadDVAttachmentList
import org.gag.appdriver.App.DataModels.DownloadDisbursement
import org.gag.appdriver.App.DataModels.DownloadDisbursementList
import org.gag.appdriver.App.DataModels.DownloadError
import org.gag.appdriver.App.DataModels.DownloadKey
import org.gag.appdriver.Constants.API_CONSTANTS
import org.gag.appdriver.Constants.FILE_CONSTANTS
import org.gag.appdriver.Libraries.DateUtil.DateRepository
import org.gag.appdriver.Libraries.Encryption.HashRepository
import org.gag.appdriver.Libraries.File.FileHelper
import org.gag.appdriver.Libraries.HTTP.KTORepository
import org.gag.appdriver.Libraries.Preferences.AppConfig
import org.gag.appdriver.Libraries.TextLibrary.TextFormatter
import org.gag.appdriver.Room.DataObject.DDisbursement
import org.gag.appdriver.Room.DataObject.DUserInfo
import org.gag.appdriver.Room.Entities.EDisbursement
import org.gag.appdriver.Room.Entities.EDisbursementAttachment
import org.gag.appdriver.Room.Entities.EUserInfo
import org.gag.appdriver.Room.ML_DBF
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
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

    fun GetDisbursementInfo(fsTransNox : String) : EDisbursement = poDisbursement.GetDisbursementInfo(fsTransNox)

    fun GetDisbursement(fsTransNox : String) : LiveData<EDisbursement> = poDisbursement.GetDisbursement(fsTransNox)

    fun GetDisbursementList(fDfromxx : String, fDtoxx : String) : LiveData<List<EDisbursement>> = poDisbursement.GetDisbursementList(fDfromxx, fDtoxx)

    fun GetAttachments(fsSourceNo : String) : LiveData<List<EDisbursementAttachment>> = poDisbursement.GetAttachments(fsSourceNo)

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
    fun SaveDisbursement(foDisbursement: EDisbursement) : CompletableFuture<Any>{

        val future = CompletableFuture<Any>()

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

                            resultData.GetPayload()
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

    @SuppressLint("MissingPermission")
    fun SaveAttachment(foAttachment : EDisbursementAttachment) : CompletableFuture<Boolean>{

        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = try {

                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                } else {

                    val loParams = JSONObject().apply {
                        put("sSourceNo", foAttachment.sSourceNo)
                        put("sDocTitle", foAttachment.sDocTitle)
                        put("sRemarksx", foAttachment.sRemarksx)
                        put("sFileName", foAttachment.sFileName)
                        put("sFinFleNm", foAttachment.sFinFleNm)

                        //if attachment has already transaction number, add as parameter to update only
                        if (foAttachment.sTransNox.isNotEmpty()) put("sTransNox", foAttachment.sTransNox)

                        //encode file to byte array base64, php will decode it as file again
                        val loImg = File(foAttachment.sFinFleNm)
                        val encodedImage = Base64.encodeToString(loImg.readBytes(), Base64.NO_WRAP)

                        put("file_meta", encodedImage)
                    }

                    when (val response = httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_UPLOAD_DV_ATTACHMENT.fsURL,
                        loParams,
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    )) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData = Json.Default.decodeFromString<DownloadKey>(response.data.body())

                            //save attachment with initialized transaction number
                            foAttachment.sTransNox = resultData.GetPayload()
                            foAttachment.cRecdStat = "1"

                            poDisbursement.SaveAttachment(foAttachment)
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
    fun DownloadAttachmentDV(fsTransNox : String) : CompletableFuture<Boolean>{

        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = try {

                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                } else {

                    val loParams = JSONObject().apply {
                        put("sTransNox", fsTransNox)
                    }

                    when (val response = httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_DOWNLOAD_DV_ATTACHMENT.fsURL,
                        loParams,
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    )) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData = Json.Default.decodeFromString<DownloadAttachmentDV>(response.data.body())

                            val attachment_info = resultData.GetPayload()
                            poDisbursement.SaveAttachment(attachment_info.attachment_info)

                            val fileResult : FileHelper.FileResult = fileHelper.CreateFileFromByteString(attachment_info.file_meta, attachment_info.attachment_info.sFinFleNm)

                            fileResult.isSuccess
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
    fun DownloadAttachmentList(fsSourceNo : String) : CompletableFuture<Boolean>{

        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = try {

                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                } else {

                    val loParams = JSONObject().apply {
                        put("sSourceNo", fsSourceNo)
                    }

                    when (val response = httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_DOWNLOAD_DV_ATTACHMENT_LIST.fsURL,
                        loParams,
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    )) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData = Json.Default.decodeFromString<DownloadDVAttachmentList>(response.data.body())

                            val attachment_info = resultData.GetPayload()

                            //save each attachment info
                            attachment_info.forEach { attachment_info -> poDisbursement.SaveAttachment(attachment_info.attachment_info) }

                            //download each attachment files
                            attachment_info.forEach { attachment_info ->
                                File(attachment_info.attachment_info.sFinFleNm).let {

                                    //save to devie if file is not existing or not readable
                                    if (!it.exists() || !it.isFile || !it.canRead()) fileHelper.CreateFileFromByteString(attachment_info.file_meta, attachment_info.attachment_info.sFinFleNm)
                                }
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