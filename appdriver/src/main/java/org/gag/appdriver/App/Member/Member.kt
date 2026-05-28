package org.gag.appdriver.App.Member

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import io.ktor.client.call.body
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.gag.appdriver.App.DataModels.DownloadError
import org.gag.appdriver.App.DataModels.DownloadLogin
import org.gag.appdriver.App.DataModels.DownloadMemberInfo
import org.gag.appdriver.App.DataModels.DownloadUserInfo
import org.gag.appdriver.Constants.API_CONSTANTS
import org.gag.appdriver.Constants.DATE_CONSTANTS
import org.gag.appdriver.Libraries.DateUtil.DateRepository
import org.gag.appdriver.Libraries.Encryption.HashRepository
import org.gag.appdriver.Libraries.HTTP.KTORepository
import org.gag.appdriver.Libraries.Preferences.AppConfig
import org.gag.appdriver.Room.DataObject.DMemberInfo
import org.gag.appdriver.Room.Entities.EMemberAddress
import org.gag.appdriver.Room.Entities.EMemberContact
import org.gag.appdriver.Room.Entities.EMemberEmail
import org.gag.appdriver.Room.Entities.EMemberInfo
import org.gag.appdriver.Room.Entities.EMemberMaster
import org.gag.appdriver.Room.Entities.EUserInfo
import org.gag.appdriver.Room.ML_DBF
import org.json.JSONObject
import java.util.concurrent.CompletableFuture

class Member(instance : Context) {

    var message : String = "No message found"

    val session : AppConfig = AppConfig(instance)
    val loInstance : Context = instance
    val httpInstance : KTORepository = KTORepository(instance)
    val encryptObj : HashRepository = HashRepository()
    val dateObj : DateRepository = DateRepository()



    fun GetMessage() : String = message


    @SuppressLint("MissingPermission")
    fun CreateMember(
        poMemberMaster: EMemberMaster,
        poMemberAddress: List<EMemberAddress>,
        poMemberContact: List<EMemberContact>,
        poMemberEmail: List<EMemberEmail>
    ): CompletableFuture<Boolean> {

        val future = CompletableFuture<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {

            if (!httpInstance.checkDeviceConnection(loInstance)) {

                message = "No internet connection"

                Handler(Looper.getMainLooper()).post {
                    future.complete(false)
                }

                return@launch
            }

            try {

                /**
                 * MEMBER MASTER
                 */
                val loMaster = JSONObject().apply {

                    put("sMemberID", poMemberMaster.sMemberID)
                    put("sLodgeIDx", poMemberMaster.sLodgeIDx)
                    put("sGLPIDNoX", poMemberMaster.sGLPIDNoX)

                    put("sLastName", poMemberMaster.sLastName)
                    put("sFrstName", poMemberMaster.sFrstName)
                    put("sSuffixNm", poMemberMaster.sSuffixNm)
                    put("sMiddName", poMemberMaster.sMiddName)

                    put("cCivilStat", poMemberMaster.cCivilStat)
                    put("dBirthDte", poMemberMaster.dBirthDte)

                    put("cMmbrStat", poMemberMaster.cMmbrStat)
                    put("dMembrshp", poMemberMaster.dMembrshp)
                    put("dSuspendx", poMemberMaster.dSuspendx)

                    put("sTitleIDx", poMemberMaster.sTitleIDx)

                    put("dPetition", poMemberMaster.dPetition)
                    put("dInitiatn", poMemberMaster.dInitiatn)
                    put("dPassedxx", poMemberMaster.dPassedxx)
                    put("dRaisingX", poMemberMaster.dRaisingX)

                    put("sSponsor1", poMemberMaster.sSponsor1)
                    put("sSponsor2", poMemberMaster.sSponsor2)
                    put("sSponsor3", poMemberMaster.sSponsor3)

                    put("nDueBalxx", poMemberMaster.nDueBalxx)
                    put("nPrjBalxx", poMemberMaster.nPrjBalxx)

                    put("cRecdStat", poMemberMaster.cRecdStat)
                }

                /**
                 * MEMBER ADDRESS
                 */
                val laAddress = org.json.JSONArray()

                poMemberAddress.forEach { address ->

                    laAddress.put(
                        JSONObject().apply {

                            put("sAddressID", address.sAddrsIDx)
                            put("sMemberID", address.sMemberID)
                            put("sAddressx", address.sAddressx)
                            put("sTownIDxx", address.sTownIDxx)
                            put("cIsHomeAd", address.cIsHomeAd)
                            put("cRecdStat", address.cRecdStat)
                        }
                    )
                }

                /**
                 * MEMBER CONTACT
                 */
                val laContact = org.json.JSONArray()

                poMemberContact.forEach { contact ->

                    laContact.put(
                        JSONObject().apply {

                            put("sContctID", contact.sContctID)
                            put("sMemberID", contact.sMemberID)
                            put("sContctNo", contact.sContctNo)
                            put("sRemarksx", contact.sRemarksx)
                            put("cRecdStat", contact.cRecdStat)
                        }
                    )
                }

                /**
                 * MEMBER EMAIL
                 */
                val laEmail = org.json.JSONArray()

                poMemberEmail.forEach { email ->

                    laEmail.put(
                        JSONObject().apply {

                            put("sMailIDxx", email.sMailIDxx)
                            put("sMemberID", email.sMemberID)
                            put("sEmailAdd", email.sEmailAdd)
                            put("cRecdStat", email.cRecdStat)
                        }
                    )
                }

                /**
                 * FINAL PARAMS
                 */
                val loParams = JSONObject().apply {

                    put("master", loMaster)
                    put("address", laAddress)
                    put("contact", laContact)
                    put("email", laEmail)
                }

                httpInstance.makeRequest(
                    API_CONSTANTS.URL_BASE_SERVER.fsURL +
                            API_CONSTANTS.URL_CREATE_MEMBER.fsURL,
                    loParams
                ).let { result ->

                    when (result) {

                        is KTORepository.OnRequest.onSuccess -> {

                            Handler(Looper.getMainLooper()).post {
                                future.complete(true)
                            }
                        }

                        is KTORepository.OnRequest.onFailed -> {

                            val errorData =
                                Json.decodeFromString<DownloadError>(
                                    result.data.body()
                                )

                            message = errorData.GetError().message

                            Handler(Looper.getMainLooper()).post {
                                future.complete(false)
                            }
                        }

                        is KTORepository.OnRequest.onError<*> -> {

                            message = result.exception.toString()

                            Handler(Looper.getMainLooper()).post {
                                future.complete(false)
                            }
                        }

                        else -> {

                            message = "Invalid transaction. Could not proceed"

                            Handler(Looper.getMainLooper()).post {
                                future.complete(false)
                            }
                        }
                    }
                }

            } catch (ex: Exception) {

                message = ex.message.toString()

                Handler(Looper.getMainLooper()).post {
                    future.complete(false)
                }
            }
        }

        return future
    }
}