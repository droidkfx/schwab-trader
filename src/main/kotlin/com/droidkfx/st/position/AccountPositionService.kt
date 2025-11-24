package com.droidkfx.st.position

import com.droidkfx.st.account.Account
import com.droidkfx.st.account.AccountService
import com.droidkfx.st.strategy.StrategyEngine
import com.droidkfx.st.util.pmap
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class AccountPositionService internal constructor(
    private val accountService: AccountService,
    private val positionTargetService: PositionTargetService,
    private val positionService: PositionService,
    private val strategyEngine: StrategyEngine
) {
    private val logger = logger {}

    suspend fun getAccountPositions(): List<AccountPosition> {
        logger.trace { "getAccountPositions" }
        return mapAccountToAccountPosition(accountService.listAccounts())
    }

    suspend fun mapAccountToAccountPosition(accounts: List<Account>): List<AccountPosition> {
        logger.trace { "mapAccountToAccountPosition" }
        return accounts.pmap { getAccountPosition(it) }
    }

    suspend fun getAccountPosition(account: Account): AccountPosition {
        logger.trace { "getAccountPosition for account: $account" }
        val currentPositions = positionService.getCachedPositions(account.id)
        val positionTargets = positionTargetService.getAccountPositionTargets(account.id)
        val recommendations = strategyEngine.buildRecommendations(
            currentPositions.positions, positionTargets, currentPositions.accountCash
        )
        return AccountPosition(
            account, positionTargets, currentPositions.positions, recommendations, currentPositions.accountCash
        )
    }

    suspend fun refreshAccountPosition(ap: AccountPosition): AccountPosition {
        logger.trace { "refreshCurrentAccountPositions" }
        val currentPositions = positionService.refreshAccountPositions(ap.account)
        val newRecommendations = strategyEngine.buildRecommendations(
            currentPositions.positions,
            ap.positionTargets,
            currentPositions.accountCash
        )
        return ap.copy(
            currentPositions = currentPositions.positions,
            currentRecommendedChanges = newRecommendations,
            currentCash = currentPositions.accountCash
        )
    }

    suspend fun updateAccountPositionTargets(accountId: String, newPositions: List<PositionTarget>): AccountPosition {
        logger.trace { "updateAccountPositions $accountId" }
        positionTargetService.updateAccountPositionTargets(accountId, newPositions)
        return getAccountPosition(accountService.getAccount(accountId))
    }

    fun clear() {
        logger.trace { "clear" }
        positionTargetService.clear()
        positionService.clear()
    }
}