package com.droidkfx.st.position

import io.github.oshai.kotlinlogging.KotlinLogging.logger

internal class PositionService(private val positionRepository: PositionRepository) {
    private val logger = logger {}
    fun getAccountPosition(accountId: String): List<PositionTarget> {
        logger.trace { "getAccountPosition for account: $accountId" }
        return positionRepository.loadPositions(accountId)
    }

    fun updateAccountPositions(accountId: String, newPositions: List<PositionTarget>) {
        logger.trace { "updateAccountPositions for account: $accountId" }
        positionRepository.savePositions(accountId, newPositions)
    }
}