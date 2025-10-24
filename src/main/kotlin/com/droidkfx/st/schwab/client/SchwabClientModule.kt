package com.droidkfx.st.schwab.client

import com.droidkfx.st.config.SchwabClientConfig
import com.droidkfx.st.util.databind.ValueDataBinding
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java

class SchwabClientModule(
    config: SchwabClientConfig,
    oathAccessToken: ValueDataBinding<String?> = ValueDataBinding(null),
    requestRefresh: ValueDataBinding<Boolean>,
) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    private val client = HttpClient(Java)

    val oathClient = OauthClient(
        config = config,
        client = client
    )
    val accountsClient = AccountsClient(
        config = config,
        client = client,
        oathToken = oathAccessToken,
        requestTokenRefresh = requestRefresh
    )
    val ordersClient = OrdersClient(
        config = config,
        client = client,
        oathToken = oathAccessToken,
        requestTokenRefresh = requestRefresh
    )
    val transactionsClient = TransactionsClient()
    val userPreferenceClient = UserPreferenceClient()
}