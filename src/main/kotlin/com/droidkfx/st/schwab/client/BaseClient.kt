package com.droidkfx.st.schwab.client

import com.droidkfx.st.config.SchwabClientConfig
import com.droidkfx.st.databind.DataBinding
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.http.HttpMethod
import io.ktor.http.path
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

abstract class BaseClient(
    protected val config: SchwabClientConfig,
    protected val client: HttpClient,
    protected val oathToken: DataBinding<String?> = DataBinding(null),
    protected val defaultPathSegments: List<String> = emptyList()
) {
    protected val authorization: String
        get() = "Bearer ${oathToken.value}"

    protected val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    protected inline fun <reified T> get(
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): ApiResponse<T> =
        runBlocking {
            try {
                val resp = client.get {
                    method = HttpMethod.Get
                    url {
                        protocol = io.ktor.http.URLProtocol.HTTPS
                        host = config.baseApiUrl
                        path(*defaultPathSegments.toTypedArray())
                    }

                    headers.append("Authorization", authorization)
                    headers.append("Accept", "application/json")

                    block()
                }

                val respBody = resp.body<String>()
                if (resp.status.value in 200..299) {
                    ApiResponse(json.decodeFromString<T>(respBody))
                } else if (respBody == "") {
                    ApiResponse(error = ErrorResponse(emptyList(), "${resp.status.value} ${resp.status.description}"))
                } else {
                    ApiResponse(error = json.decodeFromString(respBody))
                }
            } catch (e: Exception) {
                ApiResponse(error = ErrorResponse(emptyList(), e.message ?: "Unknown error"))
            }
        }

    protected inline fun <reified T> getAt(
        vararg segments: String = emptyArray()
    ): ApiResponse<T> =
        get {
            url {
                path(*defaultPathSegments.toTypedArray(), *segments)
            }
        }
}

data class ApiResponse<T>(val data: T? = null, val error: ErrorResponse? = null)

@Serializable
data class ErrorResponse(val errors: List<JsonObject>? = emptyList(), val message: String? = null)