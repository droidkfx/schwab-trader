package com.droidkfx.st.transaction

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.koin.core.context.GlobalContext

class TransactionModule() {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    val transactionService = TransactionService(GlobalContext.get().get())
}