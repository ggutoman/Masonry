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
import org.gag.appdriver.App.DataModels.DownloadKey
import org.gag.appdriver.App.DataModels.DownloadProjectDetail
import org.gag.appdriver.App.DataModels.DownloadProjectInfo
import org.gag.appdriver.App.DataModels.DownloadProjects
import org.gag.appdriver.App.Models.LodgeCalendarList
import org.gag.appdriver.App.Models.ProjectDetail
import org.gag.appdriver.Constants.API_CONSTANTS
import org.gag.appdriver.Libraries.DateUtil.DateRepository
import org.gag.appdriver.Libraries.Encryption.HashRepository
import org.gag.appdriver.Libraries.HTTP.KTORepository
import org.gag.appdriver.Libraries.Preferences.AppConfig
import org.gag.appdriver.Libraries.TextLibrary.TextFormatter
import org.gag.appdriver.Room.DataObject.DLodgeCalendar
import org.gag.appdriver.Room.DataObject.DMemberInfo
import org.gag.appdriver.Room.DataObject.DProjects
import org.gag.appdriver.Room.DataObject.DUserInfo
import org.gag.appdriver.Room.Entities.EMemberInfo
import org.gag.appdriver.Room.Entities.EProjectDetail
import org.gag.appdriver.Room.Entities.EProjectMaster
import org.gag.appdriver.Room.Entities.EUserInfo
import org.gag.appdriver.Room.ML_DBF
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date
import java.util.concurrent.CompletableFuture

class Projects(instance : Context) {

    lateinit var message : String

    val loInstance : Context = instance
    val session : AppConfig = AppConfig(instance)
    val httpInstance : KTORepository = KTORepository(instance)
    val dateRepository : DateRepository = DateRepository()
    val encryptObj : HashRepository = HashRepository()

    val poLodgeYear : DLodgeCalendar = ML_DBF.getDatabase(instance)?.GetLodgeCalendar() as DLodgeCalendar
    val poUserInfo : DUserInfo = ML_DBF.getDatabase(instance)?.GetUserDao() as DUserInfo
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

    fun GetProject(fsProjectCd : String) : LiveData<EProjectMaster> = poProjects.GetProjectMasterTransaction(fsProjectCd)

    fun GetProjectDetails(fsProjectCd : String) : LiveData<List<ProjectDetail>> = poProjects.GetProjectDetails(fsProjectCd)

    fun ObserveMemberList() : LiveData<List<EMemberInfo>> = poMemberInfo.ObserveMemeberList()

    fun GetProjectList(fsYearIDxx: String, fsDfrom : String, fsDto : String) : LiveData<List<EProjectMaster>> = poProjects.GetProjectMasterList(fsYearIDxx, fsDfrom, fsDto)

    @SuppressLint("MissingPermission")
    fun SaveProject(loMaster : EProjectMaster, loDetail : List<EProjectDetail>) : CompletableFuture<Boolean>{

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
                            if (loItem.sProjctCd.isNotEmpty()) put("sTransNox", loItem.sProjctCd)

                            put("sMemberID", loItem.sMemberID)
                            put("sORNoxxxx", loItem.sORNoxxxx)
                            put("dPledgexx", loItem.dPledgexx)
                            put("nPledgexx", loItem.nPledgexx)
                            put("nAmtPaidx", loItem.nAmtPaidx)
                        }
                        loDetailParams.put(obj)
                    }

                    //initialize master parameters
                    val loParams = JSONObject().apply {

                        //add transaction no to parameters, if not empty
                        if (loMaster.sProjctCd.isNotEmpty()) put("sTransNox", loMaster.sProjctCd)

                        put("sProjctNm", loMaster.sProjctNm)
                        put("cProjctTp", loMaster.cProjctTp)
                        put("sYearIDxx", loMaster.sYearIDxx)
                        put("dTransact", loMaster.dTransact)
                        put("dDueDatex", loMaster.dDueDatex)
                        put("sRemarksx", loMaster.sRemarksx)
                        put("cTranStat", loMaster.cTranStat)
                        put("detail", loDetailParams)
                    }

                    when (val response = httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_SAVE_PROJECT.fsURL,
                        loParams,
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    )) {
                        is KTORepository.OnRequest.onSuccess -> {
                            val resultData = Json.Default.decodeFromString<DownloadKey>(response.data.body())

                            //save master with generated key
                            loMaster.sProjctCd = resultData.GetPayload()
                            poProjects.SaveMaster(loMaster)

                            //save detail
                            loDetail.forEach { loItem ->
                                loItem.sProjctCd = loMaster.sProjctCd
                                poProjects.SaveDetail(loItem)
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
    fun DownloadProjects(fsYearIDxx : String, fsDfrom : String, fsDto : String) : CompletableFuture<Boolean>{

        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                } else {

                    //initialize master parameters
                    val loParams = JSONObject().apply {

                        put("sYearIDxx", fsYearIDxx)
                        put("dFromxx", fsDfrom)
                        put("dToxx", fsDto)
                    }

                    when (val response = httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_DOWNLOAD_PROJECTS.fsURL,
                        loParams,
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    )) {
                        is KTORepository.OnRequest.onSuccess -> {
                            val resultData = Json.Default.decodeFromString<DownloadProjects>(response.data.body())

                            //save detail
                            resultData.GetPayload().forEach { loItem ->
                                poProjects.SaveMaster(loItem)
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
    fun DownloadProjectInfo(fsProjectCd : String) : CompletableFuture<Boolean>{

        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                } else {

                    //initialize master parameters
                    val loParams = JSONObject().apply {
                        put("sProjctCd", fsProjectCd)
                    }

                    when (val response = httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_DOWNLOAD_PROJECT_INFO.fsURL,
                        loParams,
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    )) {
                        is KTORepository.OnRequest.onSuccess -> {
                            val resultData = Json.Default.decodeFromString<DownloadProjectInfo>(response.data.body())

                            //save info
                            poProjects.SaveMaster(resultData.GetPayload())
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
    fun DownloadProjectDetails(fsProjectCd : String, fsMemberID : String) : CompletableFuture<Boolean>{

        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                } else {

                    //initialize master parameters
                    val loParams = JSONObject().apply {
                        put("sProjctCd", fsProjectCd)

                        //filter with member if needed
                        if (fsMemberID.isNotEmpty()) put("sMemberID", fsMemberID)
                    }

                    when (val response = httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_DOWNLOAD_PROJECT_DETAILS.fsURL,
                        loParams,
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    )) {
                        is KTORepository.OnRequest.onSuccess -> {
                            val resultData = Json.Default.decodeFromString<DownloadProjectDetail>(response.data.body())

                            //save detail
                            resultData.GetPayload().forEach { loItem -> poProjects.SaveDetail(loItem) }
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