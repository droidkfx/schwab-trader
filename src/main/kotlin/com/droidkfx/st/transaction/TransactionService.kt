package com.droidkfx.st.transaction

import com.droidkfx.st.account.Account
import com.droidkfx.st.schwab.client.Transaction
import com.droidkfx.st.schwab.client.TransactionType
import com.droidkfx.st.schwab.client.TransactionsClient
import com.droidkfx.st.util.pmap
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class TransactionService(private val transactionsClient: TransactionsClient) {
    private val logger = logger {}

    suspend fun getTransactions(
        account: Account,
        type: TransactionType,
        from: Instant,
        to: Instant
    ): List<Transaction> {
        logger.info { "Getting transactions for type: $type" }
        val response = transactionsClient.getTransactions(
            account.accountNumberHash,
            from,
            to,
            symbol = null,
            type = type
        )
        return response.data ?: emptyList()
    }

    fun getTransactionsToday(account: Account): List<Transaction> {
        logger.trace { "getTransactions" }
        return runBlocking {
            TransactionType.entries
                .pmap {
                    getTransactions(
                        account,
                        it,
                        LocalDate.now()
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant(),
                        LocalDate.now()
                            .plusDays(1)
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .minusNanos(1)
                    )
                }
                .flatten()
        }
    }
}