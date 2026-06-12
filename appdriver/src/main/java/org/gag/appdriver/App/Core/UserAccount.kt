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
import org.gag.appdriver.App.DataModels.DownloadMemberAddresses
import org.gag.appdriver.App.DataModels.DownloadMemberContact
import org.gag.appdriver.App.DataModels.DownloadMemberEmail
import org.gag.appdriver.App.DataModels.DownloadOfficerList
import org.gag.appdriver.App.Models.OfficerInfo
import org.gag.appdriver.App.Models.TownProvince
import org.gag.appdriver.Constants.API_CONSTANTS
import org.gag.appdriver.Libraries.DateUtil.DateRepository
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
import org.gag.appdriver.Room.DataObject.DTitleInfo
import org.gag.appdriver.Room.DataObject.DTownInfo
import org.gag.appdriver.Room.DataObject.DUserInfo
import org.gag.appdriver.Room.Entities.ELodgeCalendar
import org.gag.appdriver.Room.Entities.ELodgeInfo
import org.gag.appdriver.Room.Entities.EMemberAddress
import org.gag.appdriver.Room.Entities.EMemberContactInfo
import org.gag.appdriver.Room.Entities.EMemberEmailInfo
import org.gag.appdriver.Room.Entities.EMemberInfo
import org.gag.appdriver.Room.Entities.EOfficer
import org.gag.appdriver.Room.Entities.EPosition
import org.gag.appdriver.Room.Entities.ETitle
import org.gag.appdriver.Room.Entities.EUserInfo
import org.gag.appdriver.Room.ML_DBF
import org.json.JSONObject
import java.util.concurrent.CompletableFuture
import kotlin.random.Random

class UserAccount(instance : Context) {

    var message : String = "No message found"

    val session : AppConfig = AppConfig(instance)
    val loInstance : Context = instance
    val httpInstance : KTORepository = KTORepository(instance)
    val encryptObj : HashRepository = HashRepository()
    val dateObj : DateRepository = DateRepository()

    val poUserInfo : DUserInfo = ML_DBF.getDatabase(instance).GetUserDao()
    val poMemberInfo : DMemberInfo = ML_DBF.getDatabase(instance).GetMemberDao()
    val poLodgeInfo : DLodgeInfo = ML_DBF.getDatabase(instance).GetLodge()
    val poTitleInfo : DTitleInfo = ML_DBF.getDatabase(instance).GetTitle()
    val poTownInfo : DTownInfo = ML_DBF.getDatabase(loInstance)?.GetTownCity() as DTownInfo
    val poMemberAddress : DMemberAddress = ML_DBF.getDatabase(loInstance)?.GetMemberAddress() as DMemberAddress
    val poMemberContact : DMemberContact = ML_DBF.getDatabase(loInstance)?.GetMemberContact() as DMemberContact
    val poMemberEmail : DMemberEmailInfo = ML_DBF.getDatabase(loInstance)?.GetMemberEmail() as DMemberEmailInfo
    val poLodgeCalendar : DLodgeCalendar = ML_DBF.getDatabase(loInstance)?.GetLodgeCalendar() as DLodgeCalendar
    val poPosition : DPositionInfo = ML_DBF.getDatabase(loInstance)?.GetPosition() as DPositionInfo
    val poOfficer: DOfficer = ML_DBF.getDatabase(loInstance)?.GetOfficer() as DOfficer

    fun GetMessage() : String = message

    fun GetUserID() : String = TextFormatter()
        .ExtractFromCharacter(encryptObj.DecryptHex(session.getokenID()), ":")
        .getOrNull(1) ?: ""

    fun GetCurrentDate() : String = dateObj.GetCurrentDate()

    fun GetCurrentDateTime() : String = dateObj.GetCurrentDateTime()

    fun GetSession() : AppConfig = session

    fun GetMemberGLPID(fsGLPIDxx : String) : LiveData<EMemberInfo> = poMemberInfo.GetMemberInfoByGLPID(fsGLPIDxx)

    fun GetUserInfo() : LiveData<EUserInfo> = poUserInfo.ObserveUserInfo()

    fun GetLodges() : LiveData<List<ELodgeInfo>> = poLodgeInfo.ObserveLodgeList()

    fun SearchTown(fsSearch : String) : LiveData<List<TownProvince>> {
        return poTownInfo.SearchTown("%$fsSearch%")
    }

    fun GetMemberAddress(fsMemberID : String) : LiveData<List<TownProvince>> {
        return poMemberAddress.GetMemberAddress(fsMemberID)
    }

    fun GetMemberContact(fsMemberID : String) : LiveData<List<EMemberContactInfo>> = poMemberContact.GetMemberContact(fsMemberID)

    fun GetMemberEmail(fsMemberID : String) : LiveData<List<EMemberEmailInfo>> = poMemberEmail.GetMemberEmail(fsMemberID)

    fun GetEncryption() : HashRepository = encryptObj

    fun GenerateGLPID() : String {

        return (1..6)
            .map { Random.Default.nextInt(0, 10) } // digits 0–9
            .joinToString("")
    }

    fun ObserveTitleList() : LiveData<List<ETitle>> = poTitleInfo.ObserveTitleList()

    fun ObserveLodgeCalendarList(): LiveData<List<DLodgeCalendar.LodgeCalendarList>> = poLodgeCalendar.GetLodgeCalendarList()

    fun ObserveMemberList() : LiveData<List<EMemberInfo>> = poMemberInfo.ObserveMemeberList()

    fun ObserverPositionList() : LiveData<List<EPosition>> = poPosition.ObserverPositionList()

    fun ObserveOfficerInfo(fsMemberIDx : String, fsYearIDxx : String) : LiveData<EOfficer> = poOfficer.ObserveOfficeInfo(fsMemberIDx, fsYearIDxx)

    fun ObserveCurrentRole(fsMemberIDx : String) : LiveData<OfficerInfo> = poOfficer.ObserveCurrentRole(fsMemberIDx)

    @SuppressLint("MissingPermission")
    fun LoginUser(fsID: String, fsPass: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                } else {
                    val loParams = JSONObject().apply {
                        put("sUserName", fsID)
                    }

                    val lsEncrPass = encryptObj.EncryptHex(fsPass)
                    if (lsEncrPass.isEmpty()) {
                        message = "Could not generate passkey"
                        false
                    } else {
                        loParams.put("sPassword", lsEncrPass)

                        when (val response = httpInstance.makeRequest(
                            API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_LOGIN_ACCOUNT.fsURL,
                            loParams,
                            mapOf()
                        )) {
                            is KTORepository.OnRequest.onSuccess -> {
                                val resultData = Json.Default.decodeFromString<DownloadKey>(response.data.body())
                                val sTokenIDxx = resultData.GetPayload()

                                session.isLogIn("1")
                                session.setLogDate(dateObj.GetCurrentDate())
                                session.setTokenID(sTokenIDxx)

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
    fun CreateUser(poUser : EUserInfo): CompletableFuture<Boolean> {

        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val result =  try {

                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                }

                val lsEncrPass: String = encryptObj.EncryptHex(poUser.sPassword)
                if (lsEncrPass.isEmpty()) {
                    message = "Could not generate passkey"
                    false
                }
                poUser.sPassword = lsEncrPass

                val loParams = JSONObject().apply {
                    put("sUserName", poUser.sUserName)
                    put("sPassword", poUser.sPassword)
                    put("sGLPIDNoX", poUser.sGLPIDNoX)
                    put("sLastName", poUser.sLastName)
                    put("dBirthDte", poUser.dBirthDte)
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_CREATE_ACCOUNT.fsURL,
                    loParams,
                    mapOf()
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
    fun UpdateUser(poUser : EUserInfo): CompletableFuture<Boolean> {

        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val result = try {

                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                }

                val lsEncrPass: String = encryptObj.EncryptHex(poUser.sPassword)
                if (lsEncrPass.isEmpty()) {
                    message = "Could not generate passkey"
                    false
                }
                poUser.sPassword = lsEncrPass

                val loParams = JSONObject().apply {
                    put("sPassword", poUser.sPassword)
                    put("sGLPIDNoX", poUser.sGLPIDNoX)
                    put("sLastName", poUser.sLastName)
                    put("dBirthDte", poUser.dBirthDte)
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_UPDATE_USER.fsURL,
                    loParams,
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->

                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            poMemberInfo.GetMemberInfoByUserID(
                                TextFormatter()
                                    .ExtractFromCharacter(encryptObj.DecryptHex(session.getokenID()), ":") //extract token and get user id placed after colon
                                    .getOrNull(1) ?: ""
                            ).let {

                                if (it == null){

                                    message = "Could not update. Membership information not found"
                                    false
                                }

                                //update glp id, and lastname from member name
                                it.sGLPIDNoX = poUser.sGLPIDNoX
                                it.sLastName = poUser.sLastName
                                it.dBirthDte = poUser.dBirthDte

                                //update user info and member info on local data
                                poMemberInfo.SaveMemberInfo(it)
                                poUserInfo.SaveUserInfo(poUser)
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
    fun DownloadMemberAddress(fsMemberIDxx : String): CompletableFuture<Boolean> {

        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val  result = try {

                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                }

                val params : JSONObject = JSONObject().also {
                    it.put("sMemberID", fsMemberIDxx)
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_GET_MEMBER_ADDRESS.fsURL,
                    params,
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->
                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData =
                                Json.Default.decodeFromString<DownloadMemberAddresses>(result.data.body())

                            resultData.GetPayload().forEach { loItem ->
                                poMemberAddress.SaveMemberAddress(loItem)
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
    fun DownloadMemberContact(fsMemberIDxx : String): CompletableFuture<Boolean> {

        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val  result = try {

                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                }

                val params : JSONObject = JSONObject().also {
                    it.put("sMemberID", fsMemberIDxx)
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_GET_MEMBER_CONTACT.fsURL,
                    params,
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->
                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData =
                                Json.Default.decodeFromString<DownloadMemberContact>(result.data.body())

                            resultData.GetPayload().forEach { loItem ->
                                poMemberContact.SaveMemberContact(loItem)
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
    fun DownloadMemberEmail(fsMemberIDxx : String): CompletableFuture<Boolean> {

        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val  result = try {

                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                }

                val params : JSONObject = JSONObject().also {
                    it.put("sMemberID", fsMemberIDxx)
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_GET_MEMBER_EMAIL.fsURL,
                    params,
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->
                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val resultData =
                                Json.Default.decodeFromString<DownloadMemberEmail>(result.data.body())

                            resultData.GetPayload().forEach { loItem ->
                                poMemberEmail.SaveMemberEmail(loItem)
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
    fun DownloadOfficerInfo(fsMemberID : String) : CompletableFuture<Boolean> {

        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val  result = try {

                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                }

                val params : JSONObject = JSONObject().also {
                    it.put("sMemberID", fsMemberID)
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_GET_OFFICER_INFO.fsURL,
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
                                poOfficer.SaveOfficer(loItem)
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
    fun SaveMember(memberInfo: EMemberInfo) : CompletableFuture<Any> {

        val future = CompletableFuture<Any>()
        CoroutineScope(Dispatchers.IO).launch {

            val result : Any = try {

                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                }

                val sLodgeIDx : String = TextFormatter()
                    .ExtractFromCharacter(encryptObj.DecryptHex(session.getokenID()), ":")
                    .getOrNull(0) ?: ""

                val loParams = JSONObject().apply {

                    //this is identifier for new member or update member
                    if (memberInfo.sMemberID.isNotEmpty()){
                        put("sMemberID", memberInfo.sMemberID)
                    }

                    put("sGLPIDNoX", memberInfo.sGLPIDNoX)
                    put("sLodgeIDx", sLodgeIDx)
                    put("sLastName", memberInfo.sLastName)
                    put("sFrstName", memberInfo.sFrstName)
                    put("sSuffixNm", memberInfo.sSuffixNm)
                    put("sMiddName", memberInfo.sMiddName)
                    put("cCvilStat", memberInfo.cCvilStat)
                    put("dBirthDte", memberInfo.dBirthDte)
                    put("cMmbrStat", memberInfo.cMmbrStat)
                    put("dMembrshp", memberInfo.dMembrshp)
                    put("dSuspendx", memberInfo.dSuspendx)
                    put("sTitleIDx", memberInfo.sTitleIDx)
                    put("dPetition", memberInfo.dPetition)
                    put("dInitiatn", memberInfo.dInitiatn)
                    put("dPassedxx", memberInfo.dPassedxx)
                    put("dRaisingX", memberInfo.dRaisingX)
                    put("sSponsor1", memberInfo.sSponsor1)
                    put("sSponsor2", memberInfo.sSponsor2)
                    put("sSponsor3", memberInfo.sSponsor3)
                    put("cRecdStat", memberInfo.cRecdStat)
                }


                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_CREATE_MEMBER.fsURL,
                    loParams,
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->

                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            val loResult = Json.Default.decodeFromString<DownloadKey>(result.data.body())

                            //initialize new member id, if not initialized
                            if (memberInfo.sMemberID.isNullOrEmpty()){
                                memberInfo.sMemberID = loResult.GetPayload()
                            }

                            poMemberInfo.SaveMemberInfo(memberInfo)
                            loResult.GetPayload()
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
    fun SaveMemberAddress(loAddress: EMemberAddress)  : CompletableFuture<Boolean> {

        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val result = try {

                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                }

                val loParams = JSONObject().apply {

                    //this is identifier for new member or update member
                    if (loAddress.sAddrsIDx.isNotEmpty()){
                        put("sAddrsIDx", loAddress.sAddrsIDx)
                    }

                    put("sMemberID", loAddress.sMemberID.toString())
                    put("sAddressx", loAddress.sAddressx.toString())
                    put("sTownIDxx", loAddress.sTownIDxx.toString())
                    put("cIsHomeAd", loAddress.cIsHomeAd.toString())
                    put("cRecdStat", loAddress.cRecdStat.toString())
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_CREATE_ADDRESS.fsURL,
                    loParams,
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->

                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            //initialize new address id, if not initialized
                            if (loAddress.sAddrsIDx.isNullOrEmpty()){
                                val loResult = Json.Default.decodeFromString<DownloadKey>(result.data.body())
                                loAddress.sAddrsIDx = loResult.GetPayload()
                            }
                            poMemberAddress.SaveMemberAddress(loAddress)
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
    fun SaveMemberContact(laContact: EMemberContactInfo)  : CompletableFuture<Boolean> {

        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val result = try {

                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                }

                val loParams = JSONObject().apply {
                    // identifier for new member or update member
                    if (laContact.sContctID.isNotEmpty()) {
                        put("sContctID", laContact.sContctID)
                    }

                    put("sMemberID", laContact.sMemberID)
                    put("sContctNo", laContact.sContctNo)
                    put("sRemarksx", laContact.sRemarksx ?: "")
                    put("cRecdStat", laContact.cRecdStat ?: "")
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_CREATE_CONTACT.fsURL,
                    loParams,
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->

                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            //initialize new contact id if not initialized
                            if (laContact.sContctID.isNullOrEmpty()){
                                val loResult = Json.Default.decodeFromString<DownloadKey>(result.data.body());
                                laContact.sContctID = loResult.GetPayload()
                            }
                            poMemberContact.SaveMemberContact(laContact)
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
    fun SaveMemberEmail(laEmail: EMemberEmailInfo) : CompletableFuture<Boolean> {

        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val result = try {

                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                }

                val loParams = JSONObject().apply {
                    // identifier for new or update
                    if (laEmail.sMailIDxx.isNotEmpty()) {
                        put("sMailIDxx", laEmail.sMailIDxx)
                    }

                    put("sMemberID", laEmail.sMemberID)
                    put("sEmailAdd", laEmail.sEmailAdd)
                    put("cRecdStat", laEmail.cRecdStat ?: "")
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_CREATE_EMAIL.fsURL,
                    loParams,
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->

                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            //initialize new email id if not initialized
                            if (laEmail.sMailIDxx.isNullOrEmpty()){
                                val loResult = Json.Default.decodeFromString<DownloadKey>(result.data.body());
                                laEmail.sMailIDxx = loResult.GetPayload()
                            }
                            poMemberEmail.SaveMemberEmail(laEmail)
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
    fun CreateLodgeCalendar(loLodgeCalendar : ELodgeCalendar) : CompletableFuture<Boolean> {

        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val result = try {

                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                }

                val loParams = JSONObject().apply {
                    put("sLodgeIDx", loLodgeCalendar.sLodgeIDx)
                    put("nYearxxxx", loLodgeCalendar.nYearxxxx)
                    put("dThruDate", loLodgeCalendar.dThruDate)
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_CREATE_LODGE_CALENDAR.fsURL,
                    loParams,
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->

                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            //initialize new year id from result
                            val loResult = Json.Default.decodeFromString<DownloadKey>(result.data.body());

                            loLodgeCalendar.sYearIDxx = loResult.GetPayload()
                            poLodgeCalendar.SaveLodgeCalendar(loLodgeCalendar)
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
    fun SaveOfficer(fOfficer : EOfficer, fsRemarks : String) : CompletableFuture<Boolean> {

        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val result = try {

                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                }

                val loParams = JSONObject().apply {
                    put("sYearIDxx", fOfficer.sYearIDxx)
                    put("sMemberID", fOfficer.sMemberID)
                    put("sPositnCd", fOfficer.sPositnCd)
                    put("cNewStatx", fOfficer.cStatusxx)
                    put("cAppointx", fOfficer.cAppointx)
                    put("sRemarksx", fsRemarks)
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_UPDATE_OFFICER.fsURL,
                    loParams,
                    mapOf(
                        "access-token" to session.getokenID()
                    )
                ).let { result ->

                    when (result) {
                        is KTORepository.OnRequest.onSuccess -> {

                            //initialize new year id from result
                            val loResult = Json.Default.decodeFromString<DownloadKey>(result.data.body());

                            fOfficer.nEntryNox = Integer.valueOf(loResult.GetPayload())
                            poOfficer.SaveOfficer(fOfficer)
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