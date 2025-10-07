package com.droidkfx.st.util.repository

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File

abstract class FileRepository(private val rootPath: String) {
    internal val logger = logger {}
    internal val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    internal inline fun <reified T> save(path: String, data: T) {
        logger.trace { "Saving data ${data!!::class} to file: $path" }
        val file = getFile(path)
        file.parentFile.mkdirs()
        file.writeText(json.encodeToString(data))
    }

    @OptIn(ExperimentalSerializationApi::class)
    internal inline fun <reified T> load(path: String): T? {
        logger.trace { "load ${T::class.simpleName} from file: $path" }
        val file = getFile(path)
        return try {
            if (file.exists()) {
                file.inputStream().use {
                    json.decodeFromStream<T>(it)
                }.also {
                    logger.debug { "${T::class} loaded" }
                }
            } else {
                logger.debug { "No file found at $path" }
                null
            }
        } catch (e: Exception) {
            logger.error(e) { "Error loading data from file: $path" }
            null
        }
    }

    fun delete(path: String) {
        val file = getFile(path)
        if (file.exists()) {
            file.delete()
        }
    }

    private fun getFile(path: String) = File("$rootPath/$path.json")
}