package com.droidkfx.st.account

import io.github.oshai.kotlinlogging.KotlinLogging.logger

class AccountModule {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    val accountService = Service()
}