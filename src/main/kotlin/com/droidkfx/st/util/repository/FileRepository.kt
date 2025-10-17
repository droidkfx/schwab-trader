package com.droidkfx.st.util.repository

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File

abstract class FileRepository(protected val rootPath: String) {
    private val logger = logger {}
    protected val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    internal inline fun <reified T> save(path: String, data: T) {
        logger.trace { "Saving data ${data!!::class} to file: $path" }
        val file = getFile(path)
        file.parentFile.mkdirs()
        file.writeText(json.encodeToString(data))
    }

    internal inline fun <reified T> load(path: String): T? {
        logger.trace { "load ${T::class.simpleName} from file: $path" }
        val file = getFile(path)
        return try {
            if (file.exists()) {
                load(file)
            } else {
                logger.debug { "No file found at $path" }
                null
            }
        } catch (e: Exception) {
            logger.error(e) { "Error loading data from file: $path" }
            null
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    internal inline fun <reified T> load(file: File): T? {
        return file.inputStream().use {
            json.decodeFromStream<T>(it)
        }.also {
            logger.debug { "${T::class} loaded" }
        }
    }

    internal inline fun <reified T> loadAll(): List<T> {
        logger.trace { "loadAll ${T::class.simpleName}" }
        val files = File(rootPath).listFiles { _, name -> name.endsWith(".json") }
        return files?.mapNotNull { load(it) } ?: emptyList()
    }

    internal fun delete(path: String) {
        val file = getFile(path)
        if (file.exists()) {
            file.delete()
        }
    }

    internal fun deleteAll() {
        val files = File(rootPath).listFiles { _, name -> name.endsWith(".json") }
        files?.forEach { it.delete() }
    }

    internal fun getFile(path: String) = File("$rootPath/$path.json")
}