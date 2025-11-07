package com.droidkfx.st.position

import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import com.droidkfx.st.util.databind.readOnlyMapped
import com.droidkfx.st.util.repository.FileRepository
import io.github.oshai.kotlinlogging.KotlinLogging.logger

internal class TargetPositionRepository(rootPath: ReadOnlyValueDataBinding<String>) :
    FileRepository(
        logger {},
        rootPath.readOnlyMapped { "$it/position/target" }
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