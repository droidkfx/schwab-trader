package com.droidkfx.st.oauth

import com.droidkfx.st.schwab.client.OauthTokenResponse
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File

class OauthRepository {
    @OptIn(ExperimentalSerializationApi::class)
    fun loadExistingToken(): OauthTokenResponse? {
        return try {
            val tokenFile = File("./creds/oauth-token.json")
            if (tokenFile.exists()) {
                tokenFile.inputStream().use {
                    Json.decodeFromStream<OauthTokenResponse>(it)
                }
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error loading existing token: ${e.message}")
            null
        }
    }

    fun saveToken(token: OauthTokenResponse) {
        val tokenFile = File("./creds/oauth-token.json")
        tokenFile.parentFile.mkdirs()
        tokenFile.writeText(Json.encodeToString(token))
    }

    fun deleteToken() {
        val tokenFile = File("./creds/oauth-token.json")
        tokenFile.delete()
    }
}