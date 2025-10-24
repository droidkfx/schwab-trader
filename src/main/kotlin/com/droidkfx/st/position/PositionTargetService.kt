package com.droidkfx.st.position

import io.github.oshai.kotlinlogging.KotlinLogging.logger

internal class PositionTargetService(private val positionRepository: PositionRepository) {
    private val logger = logger {}
    fun getAccountPositionTargets(accountId: String): List<PositionTarget> {
        logger.trace { "getAccountPosition for account: $accountId" }
        return positionRepository.loadPositions(accountId)
    }

    fun updateAccountPositionTargets(accountId: String, newPositions: List<PositionTarget>) {
        logger.trace { "updateAccountPositions for account: $accountId" }
        positionRepository.savePositions(accountId, newPositions)
    }

    fun clear() {
        logger.trace { "clear" }
        positionRepository.clear()
    }
}