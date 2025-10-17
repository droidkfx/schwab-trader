package com.droidkfx.st.position

import com.droidkfx.st.config.ConfigEntity
import com.droidkfx.st.util.repository.FileRepository
import io.github.oshai.kotlinlogging.KotlinLogging.logger

internal class PositionRepository(configEntity: ConfigEntity) :
    FileRepository("${configEntity.repositoryRoot}/positions") {
    private val logger = logger {}

    fun loadPositions(accountId: String): List<AccountPosition> {
        logger.trace { "loadPositions" }
        return load(accountId) ?: emptyList()
    }
}