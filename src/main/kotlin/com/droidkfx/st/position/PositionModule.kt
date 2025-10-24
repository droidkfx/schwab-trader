package com.droidkfx.st.position

import com.droidkfx.st.account.AccountModule
import com.droidkfx.st.config.ConfigModule
import com.droidkfx.st.schwab.client.SchwabClientModule
import com.droidkfx.st.strategy.StrategyModule
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class PositionModule(
    configModule: ConfigModule,
    accountModule: AccountModule,
    clientModule: SchwabClientModule,
    strategyModule: StrategyModule
) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    private val targetPositionRepository = TargetPositionRepository(configModule.configService.getConfig())
    private val targetPositionService = PositionTargetService(targetPositionRepository)

    private val positionRepository = PositionRepository(configModule.configService.getConfig())
    private val positionService: PositionService = PositionService(positionRepository, clientModule.accountsClient)

    val accountPositionService =
        AccountPositionService(
            accountModule.accountService,
            targetPositionService,
            positionService,
            strategyModule.defaultStrategy
        )
}