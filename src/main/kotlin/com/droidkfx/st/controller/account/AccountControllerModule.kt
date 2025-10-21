package com.droidkfx.st.controller.account

import com.droidkfx.st.account.AccountService
import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.position.AccountPositionService
import com.droidkfx.st.util.databind.ValueDataBinding
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class AccountControllerModule(
    accountPositionService: AccountPositionService,
    accountService: AccountService,
    accountData: ValueDataBinding<MutableList<AccountPosition>>
) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    private val selectedAccountName = ValueDataBinding(accountData.value.firstOrNull()?.Account?.name)

    private val manageAccountList = ManageAccountList(
        accountService,
        accountPositionService,
        selectedAccountName,
        accountData,
    )
    val manageAccountDialog =
        ManageAccountsDialog(accountData, selectedAccountName, manageAccountList, accountPositionService)
}