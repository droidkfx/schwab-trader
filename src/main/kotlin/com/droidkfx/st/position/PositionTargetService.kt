package com.droidkfx.st.position

import io.github.oshai.kotlinlogging.KotlinLogging.logger

internal class PositionTargetService(private val targetPositionRepository: TargetPositionRepository) {
    private val logger = logger {}
    fun getAccountPositionTargets(accountId: String): List<PositionTarget> {
        logger.trace { "getAccountPosition for account: $accountId" }
        return targetPositionRepository.loadTargetPositions(accountId)
    }

    fun updateAccountPositionTargets(accountId: String, newPositions: List<PositionTarget>) {
        logger.trace { "updateAccountPositions for account: $accountId" }
        targetPositionRepository.saveTargetPositions(accountId, newPositions)
    }

    fun clear() {
        logger.trace { "clear" }
        targetPositionRepository.clear()
    }
}