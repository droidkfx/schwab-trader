package com.droidkfx.st.position

import com.droidkfx.st.account.AccountModule
import com.droidkfx.st.config.ConfigModule
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class PositionModule(configModule: ConfigModule, accountModule: AccountModule) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    private val positionRepository = PositionRepository(configModule.configService.getConfig())
    private val positionService = PositionTargetService(positionRepository)
    val accountPositionService = AccountPositionService(accountModule.accountService, positionService)
}