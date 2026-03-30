package com.droidkfx.st.view.model

import com.droidkfx.st.account.AccountService
import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.position.AccountPositionService
import com.droidkfx.st.schwab.oauth.OauthStatus
import com.droidkfx.st.util.databind.ReadOnlyListDataBinding
import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import com.droidkfx.st.util.databind.ReadWriteListDataBinding
import com.droidkfx.st.util.databind.mapped
import com.droidkfx.st.util.databind.readOnlyMapped
import com.droidkfx.st.util.progress.ProgressService
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class AccountTabsViewModel(
    private val accountPositionService: AccountPositionService,
    private val accountService: AccountService,
    private val factory: AccountTabViewModelFactory,
    private val accountData: ReadWriteListDataBinding<AccountPosition>,
    oauthStatus: ReadOnlyValueDataBinding<OauthStatus>,
    private val progressService: ProgressService,
) {
    private val logger = logger {}

    val accountTabs: ReadOnlyListDataBinding<AccountTabViewModel> = accountData.mapped { ap ->
        factory.create(ap)
    }
    val canRefresh: ReadOnlyValueDataBinding<Boolean> = oauthStatus.readOnlyMapped { it == OauthStatus.READY }

    suspend fun refreshAllAccounts() {
        logger.debug { "refresh" }
        progressService.track("Refreshing accounts") {
            val accountPositions = accountPositionService.mapAccountToAccountPosition(accountService.refreshAccounts())
            accountPositions.forEach {
                if (!accountData.contains(it)) {
                    accountData.add(it)
                }
            }
            accountData.retainAll(accountPositions)
        }
    }
}
