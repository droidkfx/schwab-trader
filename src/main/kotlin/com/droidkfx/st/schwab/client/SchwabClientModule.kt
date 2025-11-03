package com.droidkfx.st.schwab.client

import com.droidkfx.st.config.SchwabClientConfig
import com.droidkfx.st.schwab.oauth.OauthStatus
import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import com.droidkfx.st.util.databind.ValueDataBinding
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class SchwabClientModule(
    config: SchwabClientConfig,
    oathAccessToken: ValueDataBinding<String?> = ValueDataBinding(null),
    oathAccessTokenStatus: ReadOnlyValueDataBinding<OauthStatus>,
    requestRefresh: ValueDataBinding<Boolean>,
) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
    }

    private val client = HttpClient(Java) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                coerceInputValues = true
            })
        }
    }

    val oathClient = OauthClient(
        config = config,
        client = client
    )
    val accountsClient = AccountsClient(
        config = config,
        client = client,
        oathToken = oathAccessToken,
        requestTokenRefresh = requestRefresh,
        oauthTokenStatus = oathAccessTokenStatus,
    )
    val ordersClient = OrdersClient(
        config = config,
        client = client,
        oathToken = oathAccessToken,
        requestTokenRefresh = requestRefresh,
        oauthTokenStatus = oathAccessTokenStatus,
    )
    val transactionsClient = TransactionsClient(
        config = config,
        client = client,
        oathToken = oathAccessToken,
        requestTokenRefresh = requestRefresh,
        oauthTokenStatus = oathAccessTokenStatus,
    )
    val userPreferenceClient = UserPreferenceClient()
}