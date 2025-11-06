package com.droidkfx.st.controller.account

import com.droidkfx.st.account.AccountService
import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.position.AccountPositionService
import com.droidkfx.st.util.databind.ReadWriteListDataBinding
import com.droidkfx.st.util.databind.toDataBinding
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class AccountControllerModule(
    accountPositionService: AccountPositionService,
    accountService: AccountService,
    accountData: ReadWriteListDataBinding<AccountPosition>
) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    private val selectedAccountName = accountData.firstOrNull()?.account?.name.toDataBinding()

    private val manageAccountList = ManageAccountList(
        accountService,
        accountPositionService,
        selectedAccountName,
        accountData,
    )
    val manageAccountDialog =
        ManageAccountsDialog(accountData, selectedAccountName, manageAccountList, accountPositionService)
}