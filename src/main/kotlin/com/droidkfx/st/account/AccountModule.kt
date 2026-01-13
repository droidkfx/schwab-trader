package com.droidkfx.st.account

import com.droidkfx.st.config.ConfigService
import com.droidkfx.st.schwab.SchwabModule
import com.droidkfx.st.util.databind.readOnlyMapped
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.koin.core.context.GlobalContext

class AccountModule(schwabModule: SchwabModule) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    private val configService: ConfigService by GlobalContext.get().inject()

    private val accountRepository =
        AccountRepository(configService.configDataBind.readOnlyMapped { it.repositoryRoot })

    val accountService = AccountService(accountRepository, schwabModule.clientModule.accountsClient)
}