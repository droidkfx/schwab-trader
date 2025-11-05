package com.droidkfx.st.schwab.client

import com.droidkfx.st.config.SchwabClientConfig
import com.droidkfx.st.schwab.oauth.OauthStatus
import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import com.droidkfx.st.util.databind.ValueDataBinding
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import kotlinx.serialization.Serializable

class AccountsClient(
    config: ReadOnlyValueDataBinding<SchwabClientConfig>,
    client: HttpClient,
    oathToken: ValueDataBinding<String?> = ValueDataBinding(null),
    requestTokenRefresh: ValueDataBinding<Boolean>,
    oauthTokenStatus: ReadOnlyValueDataBinding<OauthStatus>,
) : BaseClient(config, client, requestTokenRefresh, oathToken, oauthTokenStatus, listOf("trader", "v1")) {
    override val logger: KLogger = logger {}

    fun listAccountNumbers(): ApiResponse<List<AccountNumberHash>> {
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
    val securitiesAccount: SecuritiesAccount? = null
)
