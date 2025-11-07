package com.droidkfx.st.position

import com.droidkfx.st.account.AccountModule
import com.droidkfx.st.config.ConfigModule
import com.droidkfx.st.orders.OrderModule
import com.droidkfx.st.schwab.client.SchwabClientModule
import com.droidkfx.st.strategy.StrategyModule
import com.droidkfx.st.transaction.TransactionModule
import com.droidkfx.st.util.databind.readOnlyMapped
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class PositionModule(
    configModule: ConfigModule,
    accountModule: AccountModule,
    clientModule: SchwabClientModule,
    strategyModule: StrategyModule,
    orderModule: OrderModule,
    transactionModule: TransactionModule
) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    val rootPath = configModule.configService.getConfig().readOnlyMapped { it.repositoryRoot }
    private val targetPositionRepository = TargetPositionRepository(rootPath)
    private val targetPositionService = PositionTargetService(targetPositionRepository)

    private val positionRepository = PositionRepository(rootPath)
    private val positionService: PositionService = PositionService(
        positionRepository,
        clientModule.accountsClient,
        transactionModule.transactionService,
        orderModule.orderService,
    )

    val accountPositionService = AccountPositionService(
        accountModule.accountService,
        targetPositionService,
        positionService,
        strategyModule.defaultStrategy,
    )
}