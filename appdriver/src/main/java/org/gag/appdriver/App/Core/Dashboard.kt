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
import org.gag.appdriver.App.DataModels.DownloadLodgeInfo
import org.gag.appdriver.App.DataModels.DownloadMemberList
import org.gag.appdriver.App.DataModels.DownloadOfficerHistory
import org.gag.appdriver.App.DataModels.DownloadOfficerList
import org.gag.appdriver.App.DataModels.DownloadPositionInfo
import org.gag.appdriver.App.DataModels.DownloadProvinceInfo
import org.gag.appdriver.App.DataModels.DownloadTitleInfo
import org.gag.appdriver.App.DataModels.DownloadTownInfo
import org.gag.appdriver.App.DataModels.DownloadUserInfo
import org.gag.appdriver.App.Models.MemberDashboardInfo
import org.gag.appdriver.App.Models.OfficerHistory
import org.gag.appdriver.App.Models.OfficerInfo
import org.gag.appdriver.App.Models.TownProvince
import org.gag.appdriver.Constants.API_CONSTANTS
import org.gag.appdriver.Constants.MENU_ITEM_CONSTANTS
import org.gag.appdriver.Constants.MENU_PARENT_CONSTANTS
import org.gag.appdriver.Libraries.Encryption.HashRepository
import org.gag.appdriver.Libraries.HTTP.KTORepository
import org.gag.appdriver.Libraries.Preferences.AppConfig
import org.gag.appdriver.Libraries.TextLibrary.TextFormatter
import org.gag.appdriver.Room.DataObject.DLodgeCalendar
import org.gag.appdriver.Room.DataObject.DLodgeInfo
import org.gag.appdriver.Room.DataObject.DMemberAddress
import org.gag.appdriver.Room.DataObject.DMemberContact
import org.gag.appdriver.Room.DataObject.DMemberEmailInfo
import org.gag.appdriver.Room.DataObject.DMemberInfo
import org.gag.appdriver.Room.DataObject.DOfficer
import org.gag.appdriver.Room.DataObject.DOfficerHistory
import org.gag.appdriver.Room.DataObject.DPositionInfo
import org.gag.appdriver.Room.DataObject.DProvinceInfo
import org.gag.appdriver.Room.DataObject.DTitleInfo
import org.gag.appdriver.Room.DataObject.DTownInfo
import org.gag.appdriver.Room.DataObject.DUserInfo
import org.gag.appdriver.Room.Entities.ELodgeInfo
import org.gag.appdriver.Room.Entities.EMemberContactInfo
import org.gag.appdriver.Room.Entities.EMemberEmailInfo
import org.gag.appdriver.Room.Entities.EMemberInfo
import org.gag.appdriver.Room.Entities.EOfficerHistory
import org.gag.appdriver.Room.ML_DBF
import org.json.JSONObject
import java.util.concurrent.CompletableFuture

class Dashboard(loInstance : Context) {

    var message : String = "No message found"

    val session : AppConfig = AppConfig(loInstance)
    val loContext : Context = loInstance
    val httpInstance : KTORepository = KTORepository(loInstance)
    val poEncrypt : HashRepository = HashRepository()

    val poDBUser : DUserInfo = ML_DBF.getDatabase(loInstance)?.GetUserDao() as DUserInfo
    val poDBMember : DMemberInfo = ML_DBF.getDatabase(loInstance)?.GetMemberDao() as DMemberInfo
    val poDBMemberAddress : DMemberAddress = ML_DBF.getDatabase(loInstance)?.GetMemberAddress() as DMemberAddress
    val poDBMemberContact : DMemberContact = ML_DBF.getDatabase(loInstance)?.GetMemberContact() as DMemberContact
    val poDBMemberEmail : DMemberEmailInfo = ML_DBF.getDatabase(loInstance)?.GetMemberEmail() as DMemberEmailInfo
    val poLodgeInfo : DLodgeInfo = ML_DBF.getDatabase(loInstance)?.GetLodge() as DLodgeInfo
    val poPositionInfo : DPositionInfo = ML_DBF.getDatabase(loInstance)?.GetPosition() as DPositionInfo
    val poTitleInfo : DTitleInfo = ML_DBF.getDatabase(loInstance)?.GetTitle() as DTitleInfo
    val poProvinceInfo : DProvinceInfo = ML_DBF.getDatabase(loInstance)?.GetProvince() as DProvinceInfo
    val poTownInfo : DTownInfo = ML_DBF.getDatabase(loInstance)?.GetTownCity() as DTownInfo
    val poLodgeCalendar : DLodgeCalendar = ML_DBF.getDatabase(loInstance)?.GetLodgeCalendar() as DLodgeCalendar
    val poOfficers : DOfficer = ML_DBF.getDatabase(loInstance)?.GetOfficer() as DOfficer
    val poOfficerHistory : DOfficerHistory = ML_DBF.getDatabase(loInstance)?.GetOfficerHistory() as DOfficerHistory

    fun ObserverMemberInfoByUserID() : LiveData<MemberDashboardInfo> {

        return poDBMember.ObserveMemberInfoByUserID(
            TextFormatter()
                .ExtractFromCharacter(poEncrypt.DecryptHex(session.getokenID()), ":") //extract token and get user id placed after colon
                .getOrNull(1) ?: ""
        )
    }

    fun ObserveMemberList(fsMemberIDx : String, fsDateFrom : String, fsDateTo : String) : LiveData<List<EMemberInfo>> = poDBMember.ObserveMemberListByFilter(fsMemberIDx, fsDateFrom, fsDateTo)

    fun ObserveOfficersList(fsMemberIDx : String, fsDateFrom : String, fsDateTo : String) : LiveData<List<OfficerInfo>> = poOfficers.ObserveOfficerList(fsMemberIDx, fsDateFrom, fsDateTo)

    fun ObserveOfficerHistory(fsMemberIDx : String, fsDateFrom : String, fsDateTo : String) : LiveData<List<OfficerHistory>> = poOfficerHistory.ObserveOfficerHistory( fsMemberIDx, fsDateFrom, fsDateTo)

    fun GetMemberAddress(fsMemberID : String) : LiveData<List<TownProvince>> {
        return poDBMemberAddress.GetMemberAddress(fsMemberID)
    }

    fun GetMemberContact(fsMemberID: String) : LiveData<List<EMemberContactInfo>> {
        return poDBMemberContact.GetMemberContact(fsMemberID)
    }

    fun GetMemberEmail(fsMemberID: String) : LiveData<List<EMemberEmailInfo>> {
        return poDBMemberEmail.GetMemberEmail(fsMemberID)
    }

    fun GetLodgeInfo() : ELodgeInfo {

        return poLodgeInfo.GetLodgeInfo(
            TextFormatter()
                .ExtractFromCharacter(poEncrypt.DecryptHex(session.getokenID()), ":") //extract token and get user id placed after colon
                .getOrNull(0) ?: ""
        )
    }

    fun ClearMemberData(){

        poDBMember.DeleteMember()
        poDBMemberAddress.DeletMemberAddress()
        poDBMemberContact.DeleteMemberContact()
        poDBMemberEmail.DeleteMemberEmail()

        poOfficers.DeleteOfficers()
        poOfficerHistory.DeleteOfficerHistory()
    }

    @SuppressLint("MissingPermission")
    fun DownloadUserInfo(): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val result = try {

                if (!httpInstance.checkDeviceConnection(loContext)) {
                    message = "No internet connection"
                    false
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_DOWNLOAD_USER.fsURL,
                    JSONObject(),
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->
                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData =
                                Json.Default.decodeFromString<DownloadUserInfo>(result.data.body())

                            poDBUser.SaveUserInfo(resultData.GetPayload().user_info)
                            poDBMember.SaveMemberInfo(resultData.GetPayload().member_info)

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
    fun DownloadPositionInfo(): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val  result = try {

                if (!httpInstance.checkDeviceConnection(loContext)) {
                    message = "No internet connection"
                    false
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_GET_POSITION.fsURL,
                    JSONObject(),
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->
                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData =
                                Json.Default.decodeFromString<DownloadPositionInfo>(result.data.body())

                            resultData.GetPayload().forEach { loItem ->
                                poPositionInfo.SavePosition(loItem)
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
    fun DownloadTitleInfo(): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val  result = try {

                if (!httpInstance.checkDeviceConnection(loContext)) {
                    message = "No internet connection"
                    false
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_GET_TITLE.fsURL,
                    JSONObject(),
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->
                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData =
                                Json.Default.decodeFromString<DownloadTitleInfo>(result.data.body())

                            resultData.GetPayload().forEach { loItem ->
                                poTitleInfo.SaveTitle(loItem)
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
    fun DownloadProvinceInfo(): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val  result = try {

                if (!httpInstance.checkDeviceConnection(loContext)) {
                    message = "No internet connection"
                    false
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_GET_PROVINCE.fsURL,
                    JSONObject(),
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->
                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData =
                                Json.Default.decodeFromString<DownloadProvinceInfo>(result.data.body())

                            resultData.GetPayload().forEach { loItem ->
                                poProvinceInfo.SaveProvince(loItem)
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
    fun DownloadTownInfo(): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val  result = try {

                if (!httpInstance.checkDeviceConnection(loContext)) {
                    message = "No internet connection"
                    false
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_GET_TOWN.fsURL,
                    JSONObject(),
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->
                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData =
                                Json.Default.decodeFromString<DownloadTownInfo>(result.data.body())

                            resultData.GetPayload().forEach { loItem ->
                                poTownInfo.SaveTownInfo(loItem)
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
    fun DownloadLodgeCalendar(): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val  result = try {

                if (!httpInstance.checkDeviceConnection(loContext)) {
                    message = "No internet connection"
                    false
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_GET_LODGE_CALENDAR.fsURL,
                    JSONObject(),
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->
                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData =
                                Json.Default.decodeFromString<DownloadLodgeCalendar>(result.data.body())

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

    @SuppressLint("MissingPermission")
    fun DownloadMemberList(fdFromxx : String, fsDto : String): CompletableFuture<Boolean> {

        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val  result = try {

                if (!httpInstance.checkDeviceConnection(loContext)) {
                    message = "No internet connection"
                    false
                }

                val params : JSONObject = JSONObject().also {
                    it.put("dFromxx", fdFromxx)
                    it.put("dToxx", fsDto)
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_GET_MEMBERS.fsURL,
                    params,
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->
                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData =
                                Json.Default.decodeFromString<DownloadMemberList>(result.data.body())

                            resultData.GetPayload().forEach { loItem ->
                                poDBMember.SaveMemberInfo(loItem)
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
    fun DownloadOfficerList(fdFromxx : String, fsDto : String) : CompletableFuture<Boolean> {

        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val  result = try {

                if (!httpInstance.checkDeviceConnection(loContext)) {
                    message = "No internet connection"
                    false
                }

                val params : JSONObject = JSONObject().also {
                    it.put("dFromxx", fdFromxx)
                    it.put("dToxx", fsDto)
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_GET_OFFICERS.fsURL,
                    params,
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->
                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData =
                                Json.Default.decodeFromString<DownloadOfficerList>(result.data.body())

                            resultData.GetPayload().forEach { loItem ->
                                poOfficers.SaveOfficer(loItem)
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
    fun DownloadOfficerHistory(fsMemberIDx : String, fdFromxx : String, fsDto : String) : CompletableFuture<Boolean> {

        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val  result = try {

                if (!httpInstance.checkDeviceConnection(loContext)) {
                    message = "No internet connection"
                    false
                }

                val params : JSONObject = JSONObject().also {
                    it.put("sGLPIDNoX", fsMemberIDx)
                    it.put("dFromxx", fdFromxx)
                    it.put("dToxx", fsDto)
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_GET_OFFICER_HISTORY.fsURL,
                    params,
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->
                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData =
                                Json.Default.decodeFromString<DownloadOfficerHistory>(result.data.body())

                            resultData.GetPayload().forEach { loItem ->
                                poOfficerHistory.SaveOfficerHistory(loItem)
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

    fun GetParentMenus(fnUserLvl : Int) : List<MENU_PARENT_CONSTANTS>{

        return buildList{

            for (entries in MENU_PARENT_CONSTANTS.entries){

                if (entries.fnActive > 0){

                    if (fnUserLvl >= entries.fnLevel){
                        add(entries)
                    }
                }
            }

        }
    }

    fun GetParentItems(fnUserLvl : Int, fsParentIDx : String) : List<MENU_ITEM_CONSTANTS>{

        return buildList {

            for (items in MENU_ITEM_CONSTANTS.entries){

                if (items.fnActive > 0){

                    if (fsParentIDx.equals(items.fsParentIDx)){
                        if (fnUserLvl  >= items.fnLevel){

                            add(items)
                        }
                    }
                }
            }
        }
    }
}