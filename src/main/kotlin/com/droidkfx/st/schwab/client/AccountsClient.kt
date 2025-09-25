package com.droidkfx.st.schwab.client

import com.droidkfx.st.config.SchwabClientConfig
import com.droidkfx.st.databind.DataBinding
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import kotlinx.serialization.Serializable

class AccountsClient(
    config: SchwabClientConfig,
    client: HttpClient,
    oathToken: DataBinding<String?> = DataBinding(null)
) : BaseClient(config, client, oathToken, listOf("trader", "v1")) {
    override val logger: KLogger = logger {}

    fun listAccountNumbers(): ApiResponse<List<AccountNumberResponse>> {
        logger.trace { "listAccountNumbers" }
        return getAt("accounts", "accountNumbers")
    }

    fun getLinkedAccounts(includePositions: Boolean = false): ApiResponse<List<LinkedAccountsResponse>> {
        logger.trace { "getLinkedAccounts" }
        return getAt("accounts") {
            url {
                if (includePositions) {
                    parameters["fields"] = "positions"
                }
            }
        }
    }

    fun getLinkedAccount(accountId: String, includePositions: Boolean = false): ApiResponse<LinkedAccountsResponse> {
        logger.trace { "getLinkedAccount $accountId" }
        return getAt("accounts", accountId) {
            url {
                if (includePositions) {
                    parameters["fields"] = "positions"
                }
            }
        }
    }
}

@Serializable
data class LinkedAccountsResponse(
    val securitiesAccount: LinkedSecuritiesAccount
) {
    @Serializable
    data class LinkedSecuritiesAccount(
        val type: LinkedSecuritiesAccountType,
        val accountNumber: String,
        val roundTrips: Int,
        val isDayTrader: Boolean,
        val isClosingOnlyRestricted: Boolean,
        val pfcbFlag: Boolean,
    ) {
        enum class LinkedSecuritiesAccountType {
            CASH, MARGIN
        }
    }
}

@Serializable
data class AccountNumberResponse(
    val accountNumber: String,
    val hashValue: String,
)
