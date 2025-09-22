package com.droidkfx.st.oauth

import com.droidkfx.st.config.SchwabClientConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.awt.Desktop
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.io.encoding.Base64

private const val BASE_URL = "https://api.schwabapi.com/v1/oauth/authorize"

class OauthClient(
    val config: SchwabClientConfig
) {
    val client = HttpClient(Java)

    fun exchangeToken(result: LocalOAuthRedirectServer.Result): OauthTokenResponse {
        return runBlocking {
            val resp = client.post {
                url.apply {
                    host = "api.schwabapi.com"
                    protocol = URLProtocol.HTTPS
                    encodedPath = "/v1/oauth/token"
                }
                setBody("grant_type=authorization_code&code=${result.code}&redirect_uri=https://127.0.0.1:41241")
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
                BASE_URL,
                config.key,
                config.callbackServerConfig.url(),
                state
            )
        )
        return state
    }

    private fun buildAuthorizeUrl(
        baseAuthorizeUrl: String,
        clientId: String,
        redirectUri: String,
        state: String? = null,
    ): String {
        fun enc(s: String) = URLEncoder.encode(s, StandardCharsets.UTF_8)
        val params = mutableListOf(
            "client_id=${enc(clientId)}",
            "redirect_uri=${enc(redirectUri)}",
            "response_type=code",
            "state=${enc(state ?: "")}"
        )
        return "$baseAuthorizeUrl?${params.joinToString("&")}"
    }

    private fun openBrowser(url: String) {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI(url))
        } else {
            println("Open this URL in your browser: $url")
        }
    }

}