package com.droidkfx.st.position

import com.droidkfx.st.account.Account
import com.droidkfx.st.orders.OrderService
import com.droidkfx.st.schwab.client.AccountsClient
import com.droidkfx.st.schwab.client.CashAccount
import com.droidkfx.st.schwab.client.Instruction
import com.droidkfx.st.schwab.client.MarginAccount
import com.droidkfx.st.transaction.TransactionService
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.math.BigDecimal

internal class PositionService(
    private val positionRepository: PositionRepository,
    private val accountClient: AccountsClient,
    private val transactionService: TransactionService,
    private val orderService: OrderService
) {
    private val logger = logger {}
    fun getCachedPositions(id: String): CurrentPositions {
        logger.trace { "getCachedPositions for account: $id" }
        return positionRepository.loadPositions(id)
    }

    suspend fun refreshAccountPositions(account: Account): CurrentPositions {
        logger.trace { "refreshAccountPositions for account: ${account.id}" }
        val securitiesAccount = accountClient.getLinkedAccount(account.accountNumberHash, true).data
            ?.securitiesAccount
        if (securitiesAccount == null) {
            logger.error { "No securities account found for account: ${account.id}" }
        }
        var currentValue = when (securitiesAccount) {
            is MarginAccount -> securitiesAccount.initialBalances?.cashBalance ?: BigDecimal.ZERO
            is CashAccount -> securitiesAccount.initialBalances?.cashBalance ?: BigDecimal.ZERO
            else -> BigDecimal.ZERO
        }

        val transactions = transactionService.getTransactionsToday(account)
        transactions.forEach { transaction ->
            // they come in as negative
            logger.debug { "adjusting funds for transaction: ${transaction.netAmount} ${transaction.transferItems?.map { it.instrument?.symbol }}" }
            currentValue += transaction.netAmount ?: BigDecimal.ZERO
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

        val openOrders = orderService.getOpenOrders(account)
        openOrders.forEach { order ->
            logger.warn { "outstanding order: ${order}" }
            order.orderLegCollection?.forEach { legCollection ->
                legCollection.instrument?.let { instrument ->
                    positions?.firstOrNull { it.symbol == instrument.symbol }?.let { position ->
                        when (legCollection.instruction) {
                            Instruction.BUY, Instruction.BUY_TO_CLOSE, Instruction.BUY_TO_COVER -> {
                                currentValue -= (legCollection.quantity)?.times(position.lastKnownPrice)
                                    ?: BigDecimal.ZERO
                            }

                            else -> {}
                        }

                    }
                }
            }
        }
        return positions?.let { newPositions ->
            CurrentPositions(currentValue, newPositions).also {
                positionRepository.savePositions(account.id, it)
            }
        } ?: getCachedPositions(account.id).copy(accountCash = currentValue)
    }

    fun clear() {
        logger.trace { "clear" }
        positionRepository.clear()
    }

}
