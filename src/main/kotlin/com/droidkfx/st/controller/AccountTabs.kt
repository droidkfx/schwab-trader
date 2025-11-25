package com.droidkfx.st.controller

import com.droidkfx.st.account.AccountService
import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.position.AccountPositionService
import com.droidkfx.st.schwab.oauth.OauthStatus
import com.droidkfx.st.util.databind.ReadOnlyListDataBinding
import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import com.droidkfx.st.util.databind.ReadWriteListDataBinding
import com.droidkfx.st.util.databind.mapped
import com.droidkfx.st.util.databind.readOnlyMapped
import com.droidkfx.st.view.AccountTabsController
import com.droidkfx.st.view.model.AccountTabViewModel
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class AccountTabs(
    private val accountPositionService: AccountPositionService,
    private val accountService: AccountService,
    private val accountData: ReadWriteListDataBinding<AccountPosition>,
    oauthData: ReadOnlyValueDataBinding<OauthStatus>,
    override val accountTabProvider: (AccountTabViewModel) -> AccountTab,
) : AccountTabsController {
    private val logger = logger {}
    override val accountTabs: ReadOnlyListDataBinding<AccountTabViewModel> = accountData.mapped { ap ->
        AccountTabViewModel(ap)
    }
    override val canRefresh: ReadOnlyValueDataBinding<Boolean> = oauthData.readOnlyMapped { it == OauthStatus.READY }

    override suspend fun refreshAllAccounts() {
        logger.debug { "refresh" }
        val accountPositions = accountPositionService.mapAccountToAccountPosition(accountService.refreshAccounts())
        accountPositions.forEach {
            if (!accountData.contains(it)) {
                accountData.add(it)
            }
        }
        accountData.retainAll(accountPositions)
    }
}
