package com.droidkfx.st.position

import com.droidkfx.st.config.ConfigService
import com.droidkfx.st.orders.OrderModule
import com.droidkfx.st.transaction.TransactionModule
import com.droidkfx.st.util.databind.readOnlyMapped
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.koin.core.context.GlobalContext

class PositionModule(
    orderModule: OrderModule,
    transactionModule: TransactionModule
) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    private val configService: ConfigService by GlobalContext.get().inject()

    val rootPath = configService.configDataBind.readOnlyMapped { it.repositoryRoot }
    private val targetPositionRepository = TargetPositionRepository(rootPath)
    private val targetPositionService = PositionTargetService(targetPositionRepository)

    private val positionRepository = PositionRepository(rootPath)
    private val positionService: PositionService = PositionService(
        positionRepository,
        GlobalContext.get().get(),
        transactionModule.transactionService,
        orderModule.orderService,
    )

    val accountPositionService = AccountPositionService(
        GlobalContext.get().get(),
        targetPositionService,
        positionService,
        GlobalContext.get().get(),
    )
}