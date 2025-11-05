package com.droidkfx.st.schwab.client

import com.droidkfx.st.config.SchwabClientConfig
import com.droidkfx.st.schwab.oauth.OauthStatus
import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import com.droidkfx.st.util.databind.ValueDataBinding
import com.droidkfx.st.util.serialization.KInstant
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient

class TransactionsClient(
    config: ReadOnlyValueDataBinding<SchwabClientConfig>,
    client: HttpClient,
    oathToken: ValueDataBinding<String?> = ValueDataBinding(null),
    requestTokenRefresh: ValueDataBinding<Boolean>,
    oauthTokenStatus: ReadOnlyValueDataBinding<OauthStatus>,
) : BaseClient(config, client, requestTokenRefresh, oathToken, oauthTokenStatus, listOf("trader", "v1")) {
    override val logger: KLogger = logger {}

    fun getTransactions(
        accountNumber: String,
        startDate: KInstant,
        endDate: KInstant,
        symbol: String? = null,
        type: TransactionType
    ): ApiResponse<List<Transaction>> =
        getAt("accounts", accountNumber, "transactions") {
            url {
                parameters["startDate"] = startDate.toString()
                parameters["endDate"] = endDate.toString()
                parameters["types"] = type.name
                symbol?.let { parameters["symbol"] = it }
            }
        }
}