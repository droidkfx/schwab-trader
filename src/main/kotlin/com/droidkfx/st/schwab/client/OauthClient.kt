package com.droidkfx.st.schwab.client

import com.droidkfx.st.config.SchwabClientConfig
import com.droidkfx.st.oauth.LocalServer
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import java.awt.Desktop
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.UUID
import kotlin.io.encoding.Base64

class OauthClient(
    val config: SchwabClientConfig,
    val client: HttpClient,
) {
    private val logger = logger {}

    fun refreshOauth(refreshToken: String): OauthTokenResponse = exchangeOauthToken(refreshToken, "refresh_token")

    fun exchangeOauthToken(result: LocalServer.Result): OauthTokenResponse {
        if (result.code == null) {
            throw IllegalStateException("No code returned from server")
        }
        return exchangeOauthToken(result.code, "authorization_code")
    }

    fun exchangeOauthToken(
        token: String,
        grantType: String,
    ): OauthTokenResponse = runBlocking {
        client.post {
            url.apply {
                host = config.baseApiUrl
                protocol = URLProtocol.HTTPS
                encodedPath = "/v1/oauth/token"
            }
            val body = StringBuilder()
            body.append("grant_type=${grantType}")
            if (grantType == "authorization_code") {
                body.append("&code=${token}&redirect_uri=${config.callbackServerConfig.url()}")
            } else if (grantType == "refresh_token") {
                body.append("&refresh_token=${token}")
            } else {
                throw IllegalArgumentException("Unsupported grant type: $grantType")
            }

            setBody(body.toString())
            val authorization = Base64.encode("${config.key}:${config.secret}".toByteArray())
            headers.append("Authorization", "Basic $authorization")
            headers.append("Content-Type", "application/x-www-form-urlencoded")
            headers.append("Accept", "application/json")
        }.let {
            Json.decodeFromString<OauthTokenResponse>(it.body())
        }
    }

    fun triggerOauthFlow(): String {
        val state = UUID.randomUUID().toString()
        openBrowser(
            buildAuthorizeUrl(
                config.key,
                config.callbackServerConfig.url(),
                state
            )
        )
        return state
    }

    private fun buildAuthorizeUrl(
        clientId: String,
        redirectUri: String,
        state: String,
    ): String {
        val params = mutableListOf(
            "client_id=${clientId.urlEncode()}",
            "redirect_uri=${redirectUri.urlEncode()}",
            "response_type=code",
            "state=${state.urlEncode()}"
        )
        return "https://${config.baseApiUrl}/v1/oauth/authorize?${params.joinToString("&")}"
    }

    private fun openBrowser(url: String) {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI(url))
        } else {
            logger.info { "Open this URL in your browser: $url" }
        }
    }

    private fun String.urlEncode(): String {
        return URLEncoder.encode(this, StandardCharsets.UTF_8)
    }
}


@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class OauthTokenResponse(
    @JsonNames("expires_in")
    val expiresIn: Long,
    @JsonNames("token_type")
    val tokenType: String,
    @JsonNames("scope")
    val scope: String,
    @JsonNames("refresh_token")
    val refreshToken: String,
    @JsonNames("access_token")
    val accessToken: String,
    @JsonNames("id_token")
    val idToken: String
) {

    @Transient
    private val idTokenClaims = idToken
        .split(".")
        .getOrElse(1) { null }
        ?.let { Base64.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL).decode(it).decodeToString() }
        ?.let { Json.decodeFromString<JsonObject>(it) }

    @Transient
    val expiresAt = idTokenClaims
        ?.let { it["exp"]?.jsonPrimitive?.long }
        ?.let { Instant.ofEpochSecond(it) }
}