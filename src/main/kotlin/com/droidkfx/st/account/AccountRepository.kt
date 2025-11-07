package com.droidkfx.st.account

import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import com.droidkfx.st.util.databind.readOnlyMapped
import com.droidkfx.st.util.repository.FileRepository
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class AccountRepository(rootPath: ReadOnlyValueDataBinding<String>) : FileRepository(
    logger {},
    rootPath.readOnlyMapped { "$it/account" }
) {
    fun loadAccounts(): List<Account> {
        logger.trace { "loadAccounts" }
        return loadAll()
    }

    fun saveAccount(account: Account) {
        logger.trace { "saveAccount $account" }
        save(account.id, account)
    }

    fun clear() {
        logger.trace { "clear" }
        deleteAll()
    }

    fun getAccount(accountId: String): Account {
        logger.trace { "getAccount $accountId" }
        return load(accountId) ?: throw IllegalArgumentException("Account with id $accountId not found")
    }
}