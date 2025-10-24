package com.droidkfx.st.position

import com.droidkfx.st.config.ConfigEntity
import com.droidkfx.st.util.repository.FileRepository
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class PositionRepository(configEntity: ConfigEntity) :
    FileRepository(
        logger {},
        "${configEntity.repositoryRoot}/position/current"
    ) {
    fun loadPositions(id: String): List<Position> {
        logger.trace { "loadPositions for account: $id" }
        return load(id) ?: emptyList()
    }

    fun clear() {
        logger.trace { "clear" }
        deleteAll()
    }
}
