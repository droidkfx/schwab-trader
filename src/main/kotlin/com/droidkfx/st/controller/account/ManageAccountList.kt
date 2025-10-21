package com.droidkfx.st.controller.account

import com.droidkfx.st.account.AccountService
import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.position.AccountPositionService
import com.droidkfx.st.util.databind.ReadWriteListDataBinding
import com.droidkfx.st.util.databind.ValueDataBinding
import com.droidkfx.st.util.databind.mapped
import com.droidkfx.st.view.account.ManageAccountList

internal class ManageAccountList(
    val accountService: AccountService,
    val accountPositionService: AccountPositionService,
    selectedAccountName: ValueDataBinding<String?>,
    val accountData: ReadWriteListDataBinding<AccountPosition>,
) :
    ManageAccountList(selectedAccountName, accountData.mapped { it.Account.name }) {

    override suspend fun listSelectionChanged(name: String) {
        logger.debug { "listSelectionChanged: $name" }
        selectedAccountName.value = name
    }

    override suspend fun refresh() {
        logger.debug { "refresh" }
        val accountPositions = accountPositionService.getAccountPositions(accountService.refreshAccounts())
        accountPositions.forEach {
            if (!accountData.contains(it)) {
                accountData.add(it)
            }
        }
        accountData.retainAll(accountPositions)
    }
}