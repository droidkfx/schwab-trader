package com.droidkfx.st.controller

import com.droidkfx.st.account.AccountService
import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.position.AccountPositionService
import com.droidkfx.st.schwab.oauth.OauthStatus
import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import com.droidkfx.st.util.databind.ReadWriteListDataBinding
import com.droidkfx.st.util.databind.ValueDataBinding
import com.droidkfx.st.util.databind.mapped
import com.droidkfx.st.util.databind.toDataBinding
import com.droidkfx.st.view.AccountTabs
import com.droidkfx.st.view.model.AccountTabViewModel
import com.droidkfx.st.view.model.AllocationRowViewModel
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class AccountTabs(
    private val accountPositionService: AccountPositionService,
    private val accountService: AccountService,
    private val accountData: ReadWriteListDataBinding<AccountPosition>,
    oauthData: ReadOnlyValueDataBinding<OauthStatus>,
    accountTabProvider: (AccountTabViewModel) -> AccountTab,
) : AccountTabs(
    accountData.mapped { ap ->
        AccountTabViewModel(
            ap.account,
            ap.positionTargets
                .map { pTarget ->
                    val currentPosition = ap.currentPositions.firstOrNull { it.symbol == pTarget.symbol }
                    AllocationRowViewModel(
                        pTarget.symbol,
                        pTarget.allocationTarget,
                        currentPosition?.quantity ?: 0.0,
                        currentPosition?.lastKnownPrice ?: 0.0,
                        0.0,
                        "TBD",
                        0.0
                    )
                }
                .toMutableList()
                .toDataBinding(),
            ValueDataBinding(ap.currentCash)
        )
    },
    oauthData.mapped { it == OauthStatus.READY },
    accountTabProvider
) {
    private val logger = logger {}

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
