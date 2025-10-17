package com.droidkfx.st.controller.account

import com.droidkfx.st.account.AccountService
import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.position.AccountPositionService
import com.droidkfx.st.util.databind.DataBinding
import com.droidkfx.st.util.databind.mapped
import com.droidkfx.st.view.account.ManageAccountList

internal class ManageAccountList(
    val accountService: AccountService,
    val accountPositionService: AccountPositionService,
    val selectedAccountName: DataBinding<String?>,
    val accountData: DataBinding<List<AccountPosition>>,
) :
    ManageAccountList(accountData.mapped { list -> list.map { it.Account.name } }) {

    override fun listSelectionChanged(name: String) {
        logger.debug { "listSelectionChanged: $name" }
        selectedAccountName.value = name
    }

    override fun refresh() {
        logger.debug { "refresh" }
        accountData.value = accountPositionService.getAccountPositions(accountService.refreshAccounts())
    }
}