package com.droidkfx.st.position

import com.droidkfx.st.account.Account
import com.droidkfx.st.schwab.client.AccountsClient
import com.droidkfx.st.schwab.client.CashAccount
import com.droidkfx.st.schwab.client.MarginAccount
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.math.BigDecimal

internal class PositionService(
    private val positionRepository: PositionRepository,
    private val accountClient: AccountsClient,
) {
    private val logger = logger {}
    fun getCachedPositions(id: String): CurrentPositions {
        logger.trace { "getCachedPositions for account: $id" }
        return positionRepository.loadPositions(id)
    }

    fun refreshAccountPositions(account: Account): CurrentPositions {
        logger.trace { "refreshAccountPositions for account: ${account.id}" }
        val securitiesAccount = accountClient.getLinkedAccount(account.accountNumberHash, true).data
            ?.securitiesAccount
        if (securitiesAccount == null) {
            logger.error { "No securities account found for account: ${account.id}" }
        }
        val currentValue = when (securitiesAccount) {
            is MarginAccount -> securitiesAccount.initialBalances?.cashBalance ?: BigDecimal.ZERO
            is CashAccount -> securitiesAccount.initialBalances?.cashBalance ?: BigDecimal.ZERO
            else -> BigDecimal.ZERO
        }

        val positions = securitiesAccount
            ?.positions
            ?.map {
                Position(
                    it.instrument?.symbol ?: "UNKNOWN",
                    it.totalQuantity,
                    it.marketPrice
                )
            }
        return positions?.let { newPositions ->
            CurrentPositions(currentValue, newPositions).also {
                positionRepository.savePositions(account.id, it)
            }
        } ?: getCachedPositions(account.id)
    }

    fun clear() {
        logger.trace { "clear" }
        positionRepository.clear()
    }

}
