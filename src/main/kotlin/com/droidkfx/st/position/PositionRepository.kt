package com.droidkfx.st.position

import com.droidkfx.st.config.ConfigEntity
import com.droidkfx.st.util.repository.FileRepository
import io.github.oshai.kotlinlogging.KotlinLogging.logger

internal class PositionRepository(configEntity: ConfigEntity) :
    FileRepository(
        logger {},
        "${configEntity.repositoryRoot}/positions"
    ) {

    fun loadPositions(accountId: String): List<PositionTarget> {
        logger.trace { "loadPositions" }
        return load(accountId) ?: emptyList()
    }

    fun savePositions(accountId: String, newPositions: List<PositionTarget>) {
        logger.trace { "savePositions" }
        save(accountId, newPositions)
    }

    fun clear() {
        logger.trace { "clear" }
        deleteAll()
    }
}