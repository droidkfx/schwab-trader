package com.droidkfx.st.controller

import com.droidkfx.st.account.AccountService
import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.position.AccountPositionService
import com.droidkfx.st.schwab.oauth.OauthStatus
import com.droidkfx.st.util.databind.ReadOnlyDataBinding
import com.droidkfx.st.util.databind.ValueDataBinding
import com.droidkfx.st.util.databind.mapped
import com.droidkfx.st.view.AccountTab
import com.droidkfx.st.view.model.AccountTabViewModel
import com.droidkfx.st.view.model.AllocationRowViewModel
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class AccountTabs(
    private val accountPositionService: AccountPositionService,
    private val accountService: AccountService,
    private val accountData: ValueDataBinding<MutableList<AccountPosition>>,
    oauthData: ReadOnlyDataBinding<OauthStatus>,
) : AccountTab(
    accountData.mapped { data ->
        data.map { it ->
            AccountTabViewModel(it.Account.name, it.positionTargets.map {
                AllocationRowViewModel(it.symbol, it.allocationTarget, 0.0, 0.0, 0.0, "TBD", 0.0)
            })
        }
    },
    oauthData.mapped { it == OauthStatus.READY }
) {
    private val logger = logger {}

    override suspend fun refresh() {
        logger.debug { "refresh" }
        accountData.value = accountPositionService.getAccountPositions(accountService.refreshAccounts()).toMutableList()
    }
}