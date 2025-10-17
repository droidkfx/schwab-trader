package com.droidkfx.st.account

import com.droidkfx.st.config.ConfigEntity
import com.droidkfx.st.util.repository.FileRepository

class Repository(configEntity: ConfigEntity) : FileRepository("${configEntity.repositoryRoot}/account") {
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
}