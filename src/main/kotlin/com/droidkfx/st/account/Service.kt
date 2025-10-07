package com.droidkfx.st.account

import io.github.oshai.kotlinlogging.KotlinLogging.logger

class Service {
    private val logger = logger {}
    fun listAccounts(): List<Account> {
        logger.trace { "listAccounts" }
        return listOf(
            Account(
                id = "3757b694-74f4-4fd0-8836-7c388a32c95d",
                name = "Account 1",
                accountNumber = "123456789",
                accountNumberHash = "D4541250B586296FCCE5DEA4463AE17F",
            ),
            Account(
                id = "b4a84475-78db-4bca-a863-d8d4055b3f70",
                name = "Account 2",
                accountNumber = "987654321",
                accountNumberHash = "AE4F46B5D6406A0A9DDDE0547FAD9FE6",
            )
        )
    }
}