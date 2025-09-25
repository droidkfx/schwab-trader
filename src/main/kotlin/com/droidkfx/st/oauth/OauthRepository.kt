package com.droidkfx.st.oauth

import com.droidkfx.st.schwab.client.OauthTokenResponse
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File

class OauthRepository {
    private val logger = logger {}

    @OptIn(ExperimentalSerializationApi::class)
    fun loadExistingToken(): OauthTokenResponse? {
        logger.trace { "loadExistingToken" }
        return try {
            val tokenFile = File("./creds/oauth-token.json")
            if (tokenFile.exists()) {
                tokenFile.inputStream().use {
                    Json.decodeFromStream<OauthTokenResponse>(it)
                }.also {
                    logger.debug { "Existing token loaded: $it" }
                }
            } else {
                logger.debug { "No existing token found" }
                null
            }
        } catch (e: Exception) {
            logger.error(e) { "Error loading existing token" }
            null
        }
    }

    fun saveToken(token: OauthTokenResponse) {
        logger.trace { "saveToken $token" }
        val tokenFile = File("./creds/oauth-token.json")
        tokenFile.parentFile.mkdirs()
        tokenFile.writeText(Json.encodeToString(token))
    }

    fun deleteToken() {
        logger.trace { "deleteToken" }
        val tokenFile = File("./creds/oauth-token.json")
        tokenFile.delete()
    }
}