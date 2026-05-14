package org.gag.appdriver.Libraries

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HTTPRepository{

    val httpClient: HttpClient =
        HttpClient(OkHttp) {

            install(ContentNegotiation){
                json(
                    Json{
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )

                headers {

                }
            }

            install(Logging){
                level = LogLevel.BODY
            }
        }
}