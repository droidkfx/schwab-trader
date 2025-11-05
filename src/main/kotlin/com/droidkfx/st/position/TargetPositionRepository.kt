package com.droidkfx.st.position

import com.droidkfx.st.config.ConfigEntity
import com.droidkfx.st.util.databind.ValueDataBinding
import com.droidkfx.st.util.repository.FileRepository
import io.github.oshai.kotlinlogging.KotlinLogging.logger

internal class TargetPositionRepository(configEntity: ValueDataBinding<ConfigEntity>) :
    FileRepository(
        logger {},
        "${configEntity.value.repositoryRoot}/position/target"
    ) {

    fun loadTargetPositions(accountId: String): List<PositionTarget> {
        logger.trace { "loadPositions" }
        return load(accountId) ?: emptyList()
    }

    fun saveTargetPositions(accountId: String, newPositions: List<PositionTarget>) {
        logger.trace { "savePositions" }
        save(accountId, newPositions)
    }

    fun clear() {
        logger.trace { "clear" }
        deleteAll()
    }
}