package com.droidkfx.st.schwab.oauth

import com.droidkfx.st.config.ConfigEntity
import com.droidkfx.st.schwab.client.OauthTokenResponse
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File

class OauthRepository(configEntity: ConfigEntity) {
    private val logger = logger {}
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }
    private val repositoryRoot = configEntity.repositoryRoot

    private val tokenFile: File
        get() = File("${repositoryRoot}/oauth/token.json")

    @OptIn(ExperimentalSerializationApi::class)
    fun loadExistingToken(): OauthTokenResponse? {
        logger.trace { "loadExistingToken" }
        return try {
            if (tokenFile.exists()) {
                tokenFile.inputStream().use {
                    json.decodeFromStream<OauthTokenResponse>(it)
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
        tokenFile.parentFile.mkdirs()
        tokenFile.writeText(json.encodeToString(token))
    }

    fun deleteToken() {
        logger.trace { "deleteToken" }
        tokenFile.delete()
    }
}