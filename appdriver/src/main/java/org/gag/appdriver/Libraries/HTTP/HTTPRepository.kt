package org.gag.appdriver.Libraries.HTTP

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.gag.appdriver.Libraries.DateUtil.DateRepository
import org.gag.appdriver.Libraries.DeviceInfo.DeviceInfo
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class KTORepository(instance : Context) {

    val context = instance;

    @OptIn(ExperimentalSerializationApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun GetInstance(): HttpClient = HttpClient{

        install(Logging){
            level = LogLevel.ALL
        }

        install(HttpTimeout){
            connectTimeoutMillis = 5000L
            requestTimeoutMillis = 5000L
            socketTimeoutMillis = 5000L
        }

        //http response property
        install(ContentNegotiation){
            json(
                Json {
                    isLenient = true //allow unspecified json inputs (string, boolean, int, etc)
                    ignoreUnknownKeys = true //ignore unspecified JSON input properties
                    prettyPrint = false //pretty print JSON result
                    explicitNulls = true //allow encoding null properties w/o default values to be presented on result
                }
            )
        }

        install(ResponseObserver){
            onResponse {
                Log.i("KTORHttpRepository", it.body<String>().toString())
            }
        }

        //http request property. POST, PUT, GET
        install(DefaultRequest){

            //headers
            headers{
                append("Accept", "application/json")
                append("Content-Type", "application/json")
                append("g-char-request", "UTF-8")
                append("product-id", "MSNRY_APP")
                append("request_date", DateRepository().GetCurrentDate())
                append("device-id", DeviceInfo(context).GetAndroidID())
            }

        }

    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun checkDeviceConnection(context: Context): Boolean{

        val networkMngr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val capabilities = networkMngr.getNetworkCapabilities(networkMngr.activeNetwork)?: return false

        return when{

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

            else -> {
                false
            }
        }

    }

    @SuppressLint("NewApi")
    suspend fun TestConnection(ipServer: String): OnRequest<HttpResponse>{

        val httpObj = GetInstance()

        try {

            val httpResponse: HttpResponse = httpObj.post(ipServer)
            return OnRequest.onSuccess(httpResponse)

        }catch (e: Exception){
            return OnRequest.onError(e.message)
        }finally {
            httpObj.close()
        }
    }

    @SuppressLint("NewApi")
    suspend fun makeRequest(url: String, params: JSONObject, extraHeaders : Map<String, String>): OnRequest<HttpResponse>{

        val ktorHttp: HttpClient = GetInstance()

        try {

            val result = ktorHttp.post(url){

                headers{
                    extraHeaders.forEach { (key, value) ->
                        append(key, value)
                    }
                }

                setBody(params.toString())
            }

            Log.i("KTORHttpRepository", result.bodyAsText())

            val jsonResult = JSONObject(result.bodyAsText())
            val status = jsonResult.get("result")

            return if (status.equals("success")){
                OnRequest.onSuccess(result)
            }else{
                OnRequest.onFailed(result)
            }

        }catch (e : Exception){
            return OnRequest.onError(e)
        }finally {
            ktorHttp.close()
        }
    }

    interface OnRequest<out T>{
        data class onSuccess<T>(val data: T): OnRequest<T>
        data class onFailed<T>(val data: T): OnRequest<T>
        data class onError<T>(val exception: T): OnRequest<Nothing>
    }
}