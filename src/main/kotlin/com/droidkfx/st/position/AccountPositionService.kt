package com.droidkfx.st.position

import com.droidkfx.st.account.Account
import com.droidkfx.st.account.AccountService
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class AccountPositionService internal constructor(
    private val accountService: AccountService,
    private val positionTargetService: PositionTargetService,
    private val positionService: PositionService
) {
    private val logger = logger {}

    fun getAccountPositions(): List<AccountPosition> {
        logger.trace { "getAccountPositions" }
        return mapAccountToAccountPosition(accountService.listAccounts())
    }

    fun mapAccountToAccountPosition(accounts: List<Account>): List<AccountPosition> {
        logger.trace { "mapAccountToAccountPosition" }
        return accounts.map(::getAccountPosition)
    }

    fun getAccountPosition(account: Account): AccountPosition {
        logger.trace { "getAccountPosition for account: $account" }
        val currentPositions = positionService.getCachedPositions(account.id)
        return AccountPosition(
            account,
            positionTargetService.getAccountPositionTargets(account.id),
            currentPositions.positions,
            currentPositions.accountCash
        )
    }

    fun refreshCurrentAccountPositions(account: Account): CurrentPositions {
        logger.trace { "refreshCurrentAccountPositions" }
        return positionService.refreshAccountPositions(account)
    }

    fun updateAccountPositionTargets(accountId: String, newPositions: List<PositionTarget>) {
        logger.trace { "updateAccountPositions $accountId" }
        positionTargetService.updateAccountPositionTargets(accountId, newPositions)
    }

    fun clear() {
        logger.trace { "clear" }
        positionTargetService.clear()
        positionService.clear()
    }
}