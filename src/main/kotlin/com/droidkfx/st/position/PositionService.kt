package com.droidkfx.st.position

import io.github.oshai.kotlinlogging.KotlinLogging.logger

internal class PositionService(private val targetPositionRepository: PositionRepository) {
    private val logger = logger {}
    fun getCachedPositions(id: String): List<Position> {
        logger.trace { "getCachedPositions for account: $id" }
        return targetPositionRepository.loadPositions(id)
    }

    fun clear() {
        logger.trace { "clear" }
        targetPositionRepository.clear()
    }

}
