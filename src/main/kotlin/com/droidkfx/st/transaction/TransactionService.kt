package com.droidkfx.st.transaction

import com.droidkfx.st.account.Account
import com.droidkfx.st.schwab.client.Transaction
import com.droidkfx.st.schwab.client.TransactionType
import com.droidkfx.st.schwab.client.TransactionsClient
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.time.LocalDate
import java.time.ZoneId

class TransactionService(private val transactionsClient: TransactionsClient) {
    private val logger = logger {}

    fun getTransactionsToday(account: Account): List<Transaction> {
        logger.trace { "getTransactions" }
        return TransactionType.entries.map {
            logger.info { "Getting transactions for type: $it" }
            val response = transactionsClient.getTransactions(
                account.accountNumberHash,
                LocalDate.now()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant(),
                LocalDate.now()
                    .plusDays(1)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .minusNanos(1),
                symbol = null,
                type = it
            )
            response.data ?: emptyList()
        }.flatten()
    }
}