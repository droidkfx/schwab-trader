package com.droidkfx.st.position

import com.droidkfx.st.account.Account
import com.droidkfx.st.account.AccountService
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class AccountPositionService internal constructor(
    private val accountService: AccountService,
    private val positionService: PositionTargetService
) {
    private val logger = logger {}

    fun getAccountPositions(): List<AccountPosition> {
        logger.trace { "getAccountPositions" }
        return getAccountPositions(accountService.listAccounts())
    }

    fun getAccountPositions(accounts: List<Account>): List<AccountPosition> {
        logger.trace { "getAccountPositions" }
        return accounts.map {
            AccountPosition(it, positionService.getAccountPositionTargets(it.id))
        }
    }

    fun updateAccountPositions(accountId: String, newPositions: List<PositionTarget>) {
        logger.trace { "updateAccountPositions $accountId" }
        positionService.updateAccountPositionTargets(accountId, newPositions)
    }

    fun clear() {
        logger.trace { "clear" }
        positionService.clear()
    }
}