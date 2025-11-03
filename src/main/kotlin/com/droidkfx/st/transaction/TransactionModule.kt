package com.droidkfx.st.transaction

import io.github.oshai.kotlinlogging.KotlinLogging.logger

class TransactionModule {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    val transactionService = TransactionService()
}