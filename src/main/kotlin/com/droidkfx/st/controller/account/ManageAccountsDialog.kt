package com.droidkfx.st.controller.account

import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.position.AccountPositionService
import com.droidkfx.st.position.PositionTarget
import com.droidkfx.st.util.databind.ReadWriteListDataBinding
import com.droidkfx.st.util.databind.ValueDataBinding
import com.droidkfx.st.util.databind.readOnly
import com.droidkfx.st.view.account.ManageAccountsDialog
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class ManageAccountsDialog internal constructor(
    val data: ReadWriteListDataBinding<AccountPosition>,
    selectedAccountName: ValueDataBinding<String?>,
    manageAccountList: ManageAccountList,
    private val accountPositionService: AccountPositionService,
) : ManageAccountsDialog(data.readOnly(), selectedAccountName, manageAccountList) {
    private val logger = logger {}

    override fun onPositionSave(
        accountId: String,
        newPositions: List<PositionTarget>
    ) {
        accountPositionService.updateAccountPositions(accountId, newPositions)

        val accountIndex = data.indexOfFirst { it.Account.id == accountId }
        if (accountIndex == -1) {
            logger.error { "Account index not found: $accountIndex" }
        }
        val originalElement = data[accountIndex]
        val newElement = AccountPosition(originalElement.Account, newPositions)
        data[accountIndex] = newElement
    }
}