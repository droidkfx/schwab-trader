package com.droidkfx.st.schwab.client

import com.droidkfx.st.config.SchwabClientConfig
import com.droidkfx.st.databind.DataBinding
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java

class SchwabClientModule(
    config: SchwabClientConfig,
    oathAccessToken: DataBinding<String?> = DataBinding(null),
) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }
    private val client = HttpClient(Java)

    val accountsClient = AccountsClient(
        config = config,
        client = client,
        oathToken = oathAccessToken
    )
    val oathClient = OauthClient(
        config = config,
        client = client
    )
    val ordersClient = OrdersClient(
        config = config,
        client = client,
        oathToken = oathAccessToken
    )
    val transactionsClient = TransactionsClient()
    val userPreferenceClient = UserPreferenceClient()
}