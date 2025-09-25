package com.droidkfx.st.config

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

class ConfigService(val resourceName: String) {
    private val logger = logger {}
    private var config: ConfigEntity? = null

    fun getConfig(): ConfigEntity {
        if (config == null) {
            config = loadConfig(resourceName)
        }
        return config!! // Asserted above
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadConfig(resourceName: String): ConfigEntity {
        logger.info { "Loading config from $resourceName" }
        val path = javaClass.getResource(resourceName)
        if (path != null) {
            return Json.decodeFromStream<ConfigEntity>(path.openStream()).also {
                logger.debug { "Config loaded: $it" }
            }
        } else {
            logger.error { "Config file not found" }
            throw IllegalArgumentException("$resourceName not found")
        }
    }

}