package com.droidkfx.st.position

import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import com.droidkfx.st.util.databind.readOnlyMapped
import com.droidkfx.st.util.repository.FileRepository
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.math.BigDecimal

class PositionRepository(rootPath: ReadOnlyValueDataBinding<String>) :
    FileRepository(
        logger {},
        rootPath.readOnlyMapped { "$it/position/current" }
    ) {
    fun loadPositions(id: String): CurrentPositions {
        logger.trace { "loadPositions for account: $id" }
        return load(id) ?: CurrentPositions(BigDecimal.ZERO, emptyList())
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
