package com.droidkfx.st.schwab.client

import com.droidkfx.st.config.SchwabClientConfig
import com.droidkfx.st.oauth.LocalOAuthRedirectServer
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNames
import java.awt.Desktop
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.io.encoding.Base64

class OauthClient(
    val config: SchwabClientConfig
) {
    val client = HttpClient(Java)

    fun exchangeToken(result: LocalOAuthRedirectServer.Result): OauthTokenResponse {
        return runBlocking {
            val resp = client.post {
                url.apply {
                    host = config.baseApiUrl
                    protocol = URLProtocol.HTTPS
                    encodedPath = "/v1/oauth/token"
                }
                setBody("grant_type=authorization_code&code=${result.code}&redirect_uri=${config.callbackServerConfig.url()}")
                val authorization = Base64.encode("${config.key}:${config.secret}".toByteArray())
                headers.append("Authorization", "Basic $authorization")
                headers.append("Content-Type", "application/x-www-form-urlencoded")
                headers.append("Accept", "application/json")
            }

            return@runBlocking Json.decodeFromString(resp.body())
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
        return "${config.baseApiUrl}/v1/oauth/authorize?${params.joinToString("&")}"
    }

    private fun openBrowser(url: String) {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI(url))
        } else {
            println("Open this URL in your browser: $url")
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
)