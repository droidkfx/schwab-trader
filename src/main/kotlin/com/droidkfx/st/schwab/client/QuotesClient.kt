package com.droidkfx.st.schwab.client

import com.droidkfx.st.config.SchwabClientConfig
import com.droidkfx.st.schwab.oauth.OauthStatus
import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import com.droidkfx.st.util.databind.ValueDataBinding
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient

class QuotesClient(
    config: ReadOnlyValueDataBinding<SchwabClientConfig>,
    client: HttpClient,
    oathToken: ValueDataBinding<String?>,
    requestTokenRefresh: ValueDataBinding<Boolean>,
    oauthTokenStatus: ReadOnlyValueDataBinding<OauthStatus>
) : BaseClient(config, client, requestTokenRefresh, oathToken, oauthTokenStatus, listOf("marketdata", "v1")) {
    override val logger: KLogger = logger {}

    fun getQuotesForSymbols(
        symbols: List<String>,
        indicative: Boolean = false
    ): ApiResponse<Map<String, QuoteResponse>> {
        logger.trace { "getQuotesForSymbols" }
        return getAt("quotes") {
            url {
                parameters.appendAll("symbols", symbols)
                parameters.append("indicative", if (indicative) "true" else "false")
            }
        }
    }
}
