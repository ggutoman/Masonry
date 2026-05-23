package org.gag.appdriver.App.Accounts

import android.annotation.SuppressLint
import android.content.Context
import com.example.autodealersapplication.android.DataModels.ApiError
import io.ktor.client.call.body
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.gag.appdriver.App.DataModels.DownloadUserInfo
import org.gag.appdriver.Constants.API_CONSTANTS
import org.gag.appdriver.Libraries.HTTP.KTORepository
import org.gag.appdriver.Room.DataObject.DUserInfo
import org.gag.appdriver.Room.ML_DBF
import org.json.JSONObject
import java.util.concurrent.CompletableFuture

class UserAccount(instance : Context) {

    var message : String = "No message found"

    val loInstance : Context = instance
    val httpInstance : KTORepository = KTORepository(instance)
    val poDBUser : DUserInfo = ML_DBF.getDatabase(loInstance)?.GetUserDao() as DUserInfo

    fun GetMessage() : String = message

    @SuppressLint("MissingPermission")
    fun LoginUser(fsID : String, fsPass : String) : CompletableFuture<Boolean>{

        val future = CompletableFuture<Boolean>()
        CoroutineScope(Dispatchers.IO).launch{

            if (!httpInstance.checkDeviceConnection(loInstance)){
                message = "No internet connection"
                future.complete(false)
            }else{

                try {

                    val loParams = JSONObject()

                    loParams.put("sGLPIDNoX", fsID)
                    loParams.put("sPassword", fsPass)

                    httpInstance.makeRequest(API_CONSTANTS.URL_BASE_SERVER.fsURL + API_CONSTANTS.URL_LOGIN_ACCOUNT.fsURL, loParams).let {

                        when(it){

                            is KTORepository.OnRequest.onSuccess->{

                                val resultData = Json.decodeFromString<DownloadUserInfo>(it.data.body())
                                val details = resultData.GetData()

                                poDBUser.SaveUserInfo(details)

                                println("User account has been logged in $details")
                                future.complete(true)
                            }

                            is KTORepository.OnRequest.onError<*> -> {

                                message = it.exception.toString()
                                future.complete(false)
                            }

                            is KTORepository.OnRequest.onFailed-> {

                                val errorData = Json.decodeFromString<ApiError>(it.data.body())
                                message = errorData.GetError()

                                future.complete(false)
                            }

                            else->{
                                message = "Invalid transaction. Could not proceed"
                                future.complete(false)
                            }
                        }
                    }
                }catch (ex : Exception){
                    message = ex.message.toString()
                    future.complete(false)
                }

            }
        }
        return future
    }

}