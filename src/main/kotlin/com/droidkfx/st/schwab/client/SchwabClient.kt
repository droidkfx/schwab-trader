package com.droidkfx.st.schwab.client

import com.droidkfx.st.config.SchwabClientConfig
import com.droidkfx.st.databind.DataBinding
import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java

class SchwabClient(
    val config: SchwabClientConfig,
    val oathAccessToken: DataBinding<String?> = DataBinding(null),
) {
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
    val ordersClient = OrdersClient()
    val transactionsClient = TransactionsClient()
    val userPreferenceClient = UserPreferenceClient()
}