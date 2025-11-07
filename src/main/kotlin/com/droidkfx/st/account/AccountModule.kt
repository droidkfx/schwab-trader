package com.droidkfx.st.account

import com.droidkfx.st.config.ConfigModule
import com.droidkfx.st.schwab.SchwabModule
import com.droidkfx.st.util.databind.readOnlyMapped
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class AccountModule(configModule: ConfigModule, schwabModule: SchwabModule) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    private val accountRepository =
        AccountRepository(configModule.configService.getConfig().readOnlyMapped { it.repositoryRoot })

    val accountService = AccountService(accountRepository, schwabModule.clientModule.accountsClient)
}