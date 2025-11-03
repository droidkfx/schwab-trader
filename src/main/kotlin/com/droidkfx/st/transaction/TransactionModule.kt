package com.droidkfx.st.transaction

import com.droidkfx.st.schwab.client.SchwabClientModule
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class TransactionModule(schwabClientModule: SchwabClientModule) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    val transactionService = TransactionService(schwabClientModule.transactionsClient)
}