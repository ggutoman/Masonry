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
import org.gag.appdriver.App.DataModels.DownloadAnnualInfo
import org.gag.appdriver.App.DataModels.DownloadError
import org.gag.appdriver.App.DataModels.DownloadSaveFund
import org.gag.appdriver.App.Models.AnnualMembers
import org.gag.appdriver.App.Models.LodgeCalendarList
import org.gag.appdriver.Constants.API_CONSTANTS
import org.gag.appdriver.Libraries.DateUtil.DateRepository
import org.gag.appdriver.Libraries.Encryption.HashRepository
import org.gag.appdriver.Libraries.HTTP.KTORepository
import org.gag.appdriver.Libraries.Preferences.AppConfig
import org.gag.appdriver.Libraries.TextLibrary.TextFormatter
import org.gag.appdriver.Room.DataObject.DAnnualDetail
import org.gag.appdriver.Room.DataObject.DAnnualMaster
import org.gag.appdriver.Room.DataObject.DLodgeCalendar
import org.gag.appdriver.Room.Entities.EAnnualDetail
import org.gag.appdriver.Room.Entities.EAnnualMaster
import org.gag.appdriver.Room.ML_DBF
import org.json.JSONObject
import java.util.concurrent.CompletableFuture

class Annual(instance : Context) {

    lateinit var message : String

    val loInstance : Context = instance
    val session : AppConfig = AppConfig(instance)
    val httpInstance : KTORepository = KTORepository(instance)
    val dateRepository : DateRepository = DateRepository()
    val encryptObj : HashRepository = HashRepository()

    val poAnnualMaster : DAnnualMaster = ML_DBF.getDatabase(instance)?.GetAnnualMaster() as DAnnualMaster
    val poAnnualDetail : DAnnualDetail = ML_DBF.getDatabase(instance)?.GetAnnualDetail() as DAnnualDetail
    val poLodgeYear : DLodgeCalendar = ML_DBF.getDatabase(instance)?.GetLodgeCalendar() as DLodgeCalendar

    fun GetUserID() : String = TextFormatter()
        .ExtractFromCharacter(encryptObj.DecryptHex(session.getokenID()), ":")
        .getOrNull(1) ?: ""

    fun GetCurrentDate() : String = dateRepository.GetCurrentDate()

    fun GetCurrentDateTime() : String = dateRepository.GetCurrentDateTime()

    fun GetLodgeCalendars(fsLodgeIDxx : String) : LiveData<List<LodgeCalendarList>> = poLodgeYear.GetLodgeCalendarList(fsLodgeIDxx)

    fun GetAnnualMaster(fsYearIDxx : String) : LiveData<EAnnualMaster> = poAnnualMaster.GetAnnualMaster(fsYearIDxx)

    fun GetAnnualDetail(fsTransNox : String) : LiveData<List<AnnualMembers>> = poAnnualDetail.GetAnnualDetail(fsTransNox)

    @SuppressLint("MissingPermission")
    fun DownloadAnnualDue(fsYearIDxx : String) : CompletableFuture<Boolean>{

        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                if (!httpInstance.checkDeviceConnection(loInstance)) {
                    message = "No internet connection"
                    false
                } else {

                    val loParams = JSONObject().apply {
                        put("sYearIDxx", fsYearIDxx)
                    }

                    when (val response = httpInstance.makeRequest(
                        API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_DOWNLOAD_ANNUAL_DUES.fsURL,
                        loParams,
                        mapOf(
                            "access-token" to session.getokenID()
                        )
                    )) {
                        is KTORepository.OnRequest.onSuccess -> {
                            val resultData = Json.Default.decodeFromString<DownloadAnnualInfo>(response.data.body())

                            //get payload details
                            val loMaster : EAnnualMaster = resultData.GetPayload().master
                            val loDetail : List<EAnnualDetail> = resultData.GetPayload().detail

                            //save master
                            poAnnualMaster.SaveAnnualMaster(loMaster)

                            //save detail
                            loDetail.forEach { loItem ->
                                poAnnualDetail.SaveAnnualDetail(loItem)
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