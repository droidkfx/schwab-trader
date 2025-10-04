package com.droidkfx.st.controller.account

import com.droidkfx.st.position.AccountPositionService
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class AccountControllerModule(accountPositionService: AccountPositionService) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    val manageAccountDialog = ManageAccountsDialog(accountPositionService)
}