package com.droidkfx.st.schwab.client

import com.droidkfx.st.config.SchwabClientConfig
import com.droidkfx.st.schwab.oauth.OauthStatus
import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import com.droidkfx.st.util.databind.ValueDataBinding
import io.github.oshai.kotlinlogging.KLogger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

abstract class BaseClient(
    protected val config: ReadOnlyValueDataBinding<SchwabClientConfig>,
    protected val client: HttpClient,
    protected val requestTokenRefresh: ValueDataBinding<Boolean>,
    protected val oathToken: ValueDataBinding<String?> = ValueDataBinding(null),
    protected val oauthTokenStatus: ReadOnlyValueDataBinding<OauthStatus>,
    protected val defaultPathSegments: List<String> = emptyList()
) {
    protected abstract val logger: KLogger
    protected val authorization: String
        get() = "Bearer ${oathToken.value}"

    protected val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    protected inline fun <reified T> request(
        method: HttpMethod,
        crossinline block: HttpRequestBuilder.() -> Unit = {},
    ) = runBlocking {
        if (oauthTokenStatus.value == OauthStatus.EXPIRED) {
            logger.info { "Oauth token expired, attempting token refresh" }
            requestTokenRefresh.value = !requestTokenRefresh.value
        } else if (oauthTokenStatus.value != OauthStatus.READY) {
            return@runBlocking ApiResponse(error = ErrorResponse(emptyList(), "Oauth token not ready"))
        }

        var respBody: String? = null
        try {
            var resp = doRequestInternal(method, block)

            if (resp.status.value == 401) {
                logger.info { "Unauthorized, attempting token refresh" }
                requestTokenRefresh.value = !requestTokenRefresh.value
                resp = doRequestInternal(method, block)
            }

            respBody = resp.body<String>()
            if (resp.status.value in 200..299) {
                if (T::class == Unit::class) ApiResponse()
                else ApiResponse(json.decodeFromString<T>(respBody))
            } else if (respBody == "") {
                ApiResponse(error = ErrorResponse(emptyList(), "${resp.status.value} ${resp.status.description}"))
            } else {
                ApiResponse(error = json.decodeFromString(respBody))
            }
        } catch (e: Exception) {
            logger.error(e) { "Error making request" }
            logger.error { "Response body: $respBody" }
            ApiResponse(error = ErrorResponse(emptyList(), e.message ?: "Unknown error"))
        }.also {
            logger.trace { "Response: $it" }
        }
    }

    protected suspend inline fun doRequestInternal(
        method: HttpMethod,
        block: HttpRequestBuilder.() -> Unit
    ): HttpResponse = client.request {
        this.method = method
        url {
            protocol = URLProtocol.HTTPS
            host = config.value.baseApiUrl
            path(*defaultPathSegments.toTypedArray())
        }

        headers.append("Authorization", authorization)
        headers.append("Accept", "application/json")

        block()
        logger.trace { "Request: \n${this.method} ${url.buildString()}\n ${this.headers.entries()}\n ${this.body}" }
    }

    protected inline fun <reified T> get(crossinline block: HttpRequestBuilder.() -> Unit = {}): ApiResponse<T> =
        request(HttpMethod.Get, block)

    protected inline fun <reified T> post(crossinline block: HttpRequestBuilder.() -> Unit = {}): ApiResponse<T> =
        request(HttpMethod.Post) {
            contentType(ContentType.Application.Json)
            block()
        }

    protected inline fun <reified T> getAt(
        vararg segments: String = emptyArray(),
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): ApiResponse<T> =
        get {
            url {
                path(*defaultPathSegments.toTypedArray(), *segments)
            }
            block()
        }

    protected inline fun <reified T> postAt(
        vararg segments: String = emptyArray(),
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): ApiResponse<T> =
        post {
            url {
                path(*defaultPathSegments.toTypedArray(), *segments)
            }
            block()
        }
}

data class ApiResponse<T>(val data: T? = null, val error: ErrorResponse? = null)

@Serializable
data class ErrorResponse(val errors: List<JsonObject>? = emptyList(), val message: String? = null)