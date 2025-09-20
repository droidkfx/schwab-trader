package com.droidkfx.st.config

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

class ConfigService(val resourceName: String) {
    private var config: ConfigEntity? = null

    fun getConfig(): ConfigEntity {
        if (config == null) {
            config = loadConfig(resourceName)
        }
        return config!! // Asserted above
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadConfig(resourceName: String): ConfigEntity {
        val path = javaClass.getResource(resourceName)
        if (path != null) {
            return Json.decodeFromStream<ConfigEntity>(path.openStream())
        } else {
            throw IllegalArgumentException("$resourceName not found")
        }
    }

}