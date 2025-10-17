package com.droidkfx.st.account

import com.droidkfx.st.schwab.client.AccountsClient
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.util.UUID

class AccountService(private val accountRepository: Repository, private val accountsClient: AccountsClient) {
    private val logger = logger {}
    fun listAccounts(): List<Account> {
        logger.trace { "listAccounts" }
        return accountRepository.loadAccounts().sortedBy { it.name }
    }

    fun refreshAccounts(): List<Account> {
        logger.trace { "refreshAccounts" }

        val knownAccounts = listAccounts()
        // TODO handle client errors
        val newAccounts = accountsClient.listAccountNumbers()
            .data
            ?.filter { knownAccounts.none { account -> account.accountNumber == it.accountNumber } }
            ?.map {
                return@map Account(
                    id = UUID.randomUUID().toString(),
                    name = it.accountNumber,
                    accountNumber = it.accountNumber,
                    accountNumberHash = it.hashValue
                )
            }
        val totalAccounts = knownAccounts + (newAccounts ?: emptyList())
        totalAccounts.forEach(accountRepository::saveAccount)
        return totalAccounts.sortedBy { it.name }
    }
}