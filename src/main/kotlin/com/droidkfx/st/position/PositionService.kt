package com.droidkfx.st.position

import io.github.oshai.kotlinlogging.KotlinLogging.logger

class PositionService {
    private val logger = logger {}
    fun getAccountPosition(accountId: String): List<PositionTarget> {
        logger.trace { "getAccountPosition for account: $accountId" }
        if (accountId == "b4a84475-78db-4bca-a863-d8d4055b3f70") {
            return listOf(
                PositionTarget("SPY", 0.15),
                PositionTarget("FOO", 0.15),
                PositionTarget("FIZ", 0.15),
                PositionTarget("BUZZ", 0.15),
                PositionTarget("DONE", 0.40)
            )
        }
        if (accountId == "3757b694-74f4-4fd0-8836-7c388a32c95d") {
            return listOf(
                PositionTarget("SPY", 0.40),
                PositionTarget("FOO", 0.40),
                PositionTarget("FIZ", 0.20)
            )
        }
        return emptyList()
    }
}