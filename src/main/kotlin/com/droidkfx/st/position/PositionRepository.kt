package com.droidkfx.st.position

import com.droidkfx.st.config.ConfigEntity
import com.droidkfx.st.util.repository.FileRepository
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class PositionRepository(configEntity: ConfigEntity) :
    FileRepository(
        logger {},
        "${configEntity.repositoryRoot}/position/current"
    ) {
    fun loadPositions(id: String): CurrentPositions {
        logger.trace { "loadPositions for account: $id" }
        return load(id) ?: CurrentPositions(0.0, emptyList())
    }

    fun clear() {
        logger.trace { "clear" }
        deleteAll()
    }

    fun savePositions(accountId: String, positions: CurrentPositions) {
        logger.trace { "savePositions for account: $accountId" }
        save(accountId, positions)
    }
}
