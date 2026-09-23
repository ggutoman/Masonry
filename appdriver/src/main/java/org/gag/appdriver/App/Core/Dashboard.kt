package org.gag.appdriver.App.Core

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import io.ktor.client.call.body
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.gag.appdriver.App.DataModels.DownloadError
import org.gag.appdriver.App.DataModels.DownloadPositionInfo
import org.gag.appdriver.App.DataModels.DownloadProvinceInfo
import org.gag.appdriver.App.DataModels.DownloadTownInfo
import org.gag.appdriver.App.Models.MemberDashboardInfo
import org.gag.appdriver.App.Models.OfficerHistory
import org.gag.appdriver.App.Models.OfficerInfo
import org.gag.appdriver.App.Models.TownProvince
import org.gag.appdriver.Constants.API_CONSTANTS
import org.gag.appdriver.Libraries.Encryption.HashRepository
import org.gag.appdriver.Libraries.HTTP.KTORepository
import org.gag.appdriver.Libraries.Preferences.AppConfig
import org.gag.appdriver.Libraries.TextLibrary.TextFormatter
import org.gag.appdriver.Room.DataObject.DAnnualDetail
import org.gag.appdriver.Room.DataObject.DAnnualMaster
import org.gag.appdriver.Room.DataObject.DDisbursement
import org.gag.appdriver.Room.DataObject.DFundLedger
import org.gag.appdriver.Room.DataObject.DFundTurnover
import org.gag.appdriver.Room.DataObject.DLodgeCalendar
import org.gag.appdriver.Room.DataObject.DLodgeInfo
import org.gag.appdriver.Room.DataObject.DMemberAddress
import org.gag.appdriver.Room.DataObject.DMemberContact
import org.gag.appdriver.Room.DataObject.DMemberEmailInfo
import org.gag.appdriver.Room.DataObject.DMemberInfo
import org.gag.appdriver.Room.DataObject.DOfficer
import org.gag.appdriver.Room.DataObject.DOfficerHistory
import org.gag.appdriver.Room.DataObject.DPositionInfo
import org.gag.appdriver.Room.DataObject.DProjects
import org.gag.appdriver.Room.DataObject.DProvinceInfo
import org.gag.appdriver.Room.DataObject.DReceipt
import org.gag.appdriver.Room.DataObject.DTownInfo
import org.gag.appdriver.Room.DataObject.DUserInfo
import org.gag.appdriver.Room.DataObject.DUserObjects
import org.gag.appdriver.Room.Entities.EMemberContactInfo
import org.gag.appdriver.Room.Entities.EMemberEmailInfo
import org.gag.appdriver.Room.Entities.EMemberInfo
import org.gag.appdriver.Room.Entities.EPosition
import org.gag.appdriver.Room.Entities.EUserObjects
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
    val poProvinceInfo : DProvinceInfo = ML_DBF.getDatabase(loInstance)?.GetProvince() as DProvinceInfo
    val poTownInfo : DTownInfo = ML_DBF.getDatabase(loInstance)?.GetTownCity() as DTownInfo
    val poLodgeCalendar : DLodgeCalendar = ML_DBF.getDatabase(loInstance)?.GetLodgeCalendar() as DLodgeCalendar
    val poOfficers : DOfficer = ML_DBF.getDatabase(loInstance)?.GetOfficer() as DOfficer
    val poOfficerHistory : DOfficerHistory = ML_DBF.getDatabase(loInstance)?.GetOfficerHistory() as DOfficerHistory
    val poFunds : DFundTurnover = ML_DBF.getDatabase(loInstance)?.GetFundTurnOver() as DFundTurnover
    val poLedger : DFundLedger = ML_DBF.getDatabase(loInstance)?.GetFundLedger() as DFundLedger
    val poAnnualMaster: DAnnualMaster = ML_DBF.getDatabase(loInstance)?.GetAnnualMaster() as DAnnualMaster
    val poAnnualDetail : DAnnualDetail = ML_DBF.getDatabase(loInstance)?.GetAnnualDetail() as DAnnualDetail
    val poProjects: DProjects = ML_DBF.getDatabase(loInstance)?.GetProject() as DProjects
    val poReceipts: DReceipt = ML_DBF.getDatabase(loInstance)?.GetReceipt() as DReceipt
    val poDisbursement: DDisbursement = ML_DBF.getDatabase(loInstance)?.GetDisbursement() as DDisbursement
    val poMenus : DUserObjects = ML_DBF.getDatabase(loInstance)?.GetUserObjects() as DUserObjects

    fun ObserverMemberInfoByUserID() : LiveData<MemberDashboardInfo> {

        return poDBMember.ObserveMemberInfoByUserID(
            TextFormatter()
                .ExtractFromCharacter(poEncrypt.DecryptHex(session.getokenID()), ":") //extract token and get user id placed after colon
                .getOrNull(1) ?: ""
        )
    }

    fun SearchOfficerHistory(fsCondition : String) : List<OfficerHistory>{

        val lsSql : String = "SELECT " +
                                "a.sTransNox," +
                                "a.sYearIDxx," +
                                "b.sMemberID," +
                                "c.nYearxxxx, " +
                                "(b.sFrstName || ' ' || b.sLastName) sMemberNme," +
                                "a.dTransact," +
                                "d.sPositnDs " +
                                "FROM Officer_History a " +
                                "JOIN Member_Info b ON a.sMemberID = b.sMemberID " +
                                "JOIN Lodge_Calendar c ON a.sYearIDxx = c.sYearIDxx " +
                                "JOIN  Position_Info d ON (a.sPositnCd = d.sPositnCd) " +
                            "WHERE $fsCondition " +
                            "GROUP BY a.sTransNox ORDER BY a.dTransact DESC";

        return poOfficerHistory.SearchFilterHistory(SimpleSQLiteQuery(lsSql))
    }

    fun GetMemberAddress(fsMemberID : String) : LiveData<List<TownProvince>> {
        return poDBMemberAddress.GetMemberAddress(fsMemberID)
    }

    fun GetMemberContact(fsMemberID: String) : LiveData<List<EMemberContactInfo>> {
        return poDBMemberContact.GetMemberContact(fsMemberID)
    }

    fun GetMemberEmail(fsMemberID: String) : LiveData<List<EMemberEmailInfo>> {
        return poDBMemberEmail.GetMemberEmail(fsMemberID)
    }

    fun GetUserObjects(fnUserLvl : Int) : LiveData<List<EUserObjects>> = poMenus.GetUserObjects(fnUserLvl)

    fun GetPositions() : LiveData<List<EPosition>>{
        return poPositionInfo.ObserverPositionList()
    }

    fun ClearMemberData(){

        poDBUser.DeleteUser()
        poMenus.ClearObjects()

        poLodgeInfo.DeleteLodge()
        poLodgeCalendar.DeleteLodgeCalendar()

        poFunds.DeleteFunds()
        poLedger.DeleteLedger()

        poAnnualMaster.Clear()
        poAnnualDetail.Clear()

        poProjects.DeleteMaster()
        poProjects.DeleteDetail()

        poReceipts.DeleteReceiptMaster()
        poReceipts.DeleteReceiptDetails()

        poDisbursement.DeleteDisbursements()
        poDisbursement.DeleteAttachments()

        poDBMember.DeleteMember()
        poDBMemberAddress.DeletMemberAddress()
        poDBMemberContact.DeleteMemberContact()
        poDBMemberEmail.DeleteMemberEmail()

        poOfficers.DeleteOfficers()
        poOfficerHistory.DeleteOfficerHistory()
    }

    @SuppressLint("MissingPermission")
    fun DownloadPositionInfo(): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {

            val  result = try {

                if (!httpInstance.checkDeviceConnection(loContext)) {
                    message = "No internet connection"
                    false

                }else{

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

                }else{

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

                }else{

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