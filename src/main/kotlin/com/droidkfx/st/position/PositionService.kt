package com.droidkfx.st.position

import com.droidkfx.st.account.Account
import com.droidkfx.st.schwab.client.AccountsClient
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
        val currentValue = securitiesAccount?.currentBalances?.cashBalance ?: BigDecimal.ZERO
        val positions = securitiesAccount
            ?.positions
            ?.map {
                Position(
                    it.instrument.symbol,
                    it.totalQuantity,
                    it.marketValue / it.totalQuantity
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
