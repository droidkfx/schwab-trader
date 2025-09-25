package com.droidkfx.st.schwab.client

import com.droidkfx.st.config.SchwabClientConfig
import com.droidkfx.st.databind.DataBinding
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

class OrdersClient(
    config: SchwabClientConfig,
    client: HttpClient,
    oathToken: DataBinding<String?> = DataBinding(null)
) : BaseClient(config, client, oathToken, listOf("trader", "v1")) {
    override val logger: KLogger = logger {}

    fun getAccountOrders(
        accountNumber: String,
        fromEnteredTime: OffsetDateTime,
        toEnteredTime: OffsetDateTime,
        status: String? = null,
        maxResults: Int? = null
    ): ApiResponse<List<Order>> =
        getAt("accounts", accountNumber, "orders") {
            url {
                parameters["fromEnteredTime"] = fromEnteredTime.toString()
                parameters["toEnteredTime"] = toEnteredTime.toString()
                status?.let { parameters["status"] = it }
                maxResults?.let { parameters["maxResults"] = it.toString() }
            }
        }
}

@Serializable
data class Order(val tage: String, val accountNumber: String)