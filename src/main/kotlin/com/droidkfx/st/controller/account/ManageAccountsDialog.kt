package com.droidkfx.st.controller.account

import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.position.AccountPositionService
import com.droidkfx.st.position.PositionTarget
import com.droidkfx.st.util.databind.DataBinding
import com.droidkfx.st.util.databind.mapped
import com.droidkfx.st.view.account.ManageAccountsDialog
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class ManageAccountsDialog internal constructor(
    val data: DataBinding<MutableList<AccountPosition>>,
    selectedAccountName: DataBinding<String?>,
    manageAccountList: ManageAccountList,
    private val accountPositionService: AccountPositionService,
) : ManageAccountsDialog(data.mapped { it as List<AccountPosition> }, selectedAccountName, manageAccountList) {
    private val logger = logger {}

    override fun onPositionSave(
        accountId: String,
        newPositions: List<PositionTarget>
    ) {
        accountPositionService.updateAccountPositions(accountId, newPositions)

        val accountIndex = data.value.indexOfFirst { it.Account.id == accountId }
        if (accountIndex == -1) {
            logger.error { "Account index not found: $accountIndex" }
        }
        val originalElement = data.value[accountIndex]
        val newElement = AccountPosition(originalElement.Account, newPositions)
        data.value[accountIndex] = newElement
        data.notifyChanged()
    }
}