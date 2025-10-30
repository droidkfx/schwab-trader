package com.droidkfx.st.schwab.client

import com.droidkfx.st.account.Account
import com.droidkfx.st.config.SchwabClientConfig
import com.droidkfx.st.schwab.oauth.OauthStatus
import com.droidkfx.st.strategy.PositionRecommendation
import com.droidkfx.st.strategy.StrategyAction
import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import com.droidkfx.st.util.databind.ValueDataBinding
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import java.time.OffsetDateTime

class OrdersClient(
    config: SchwabClientConfig,
    client: HttpClient,
    oathToken: ValueDataBinding<String?> = ValueDataBinding(null),
    requestTokenRefresh: ValueDataBinding<Boolean>,
    oauthTokenStatus: ReadOnlyValueDataBinding<OauthStatus>,
) : BaseClient(config, client, requestTokenRefresh, oathToken, oauthTokenStatus, listOf("trader", "v1")) {
    override val logger: KLogger = logger {}

    fun getAccountOrders(
        accountNumber: String,
        fromEnteredTime: OffsetDateTime,
        toEnteredTime: OffsetDateTime,
        status: String? = null,
        maxResults: Int? = null
    ): ApiResponse<List<Order>> = getAt("accounts", accountNumber, "orders") {
        url {
            parameters["fromEnteredTime"] = fromEnteredTime.toString()
            parameters["toEnteredTime"] = toEnteredTime.toString()
            status?.let { parameters["status"] = it }
            maxResults?.let { parameters["maxResults"] = it.toString() }
        }
    }

    fun order(account: Account, recommendation: PositionRecommendation): ApiResponse<Unit> =
        postAt("accounts", account.accountNumberHash, "orders") {
//            val quantity = recommendation.quantity.setScale(0, RoundingMode.FLOOR)
            val quantity = recommendation.quantity
            val body = OrderRequest(
                orderType = OrderTypeRequest.MARKET,
                session = Session.NORMAL,
                duration = Duration.DAY,
                orderStrategyType = OrderStrategyType.SINGLE,
                orderLegCollection = listOf(
                    OrderLegCollection(
                        instruction = recommendation.recommendation.toInstruction(),
                        quantity = quantity,
                        instrument = TransactionEquity(symbol = recommendation.symbol)
                    )
                ),
            )
            setBody(body)
        }

    fun previewOrder(account: Account, recommendation: PositionRecommendation): ApiResponse<PreviewOrder> =
        postAt("accounts", account.accountNumberHash, "previewOrder") {
//            val quantity = recommendation.quantity.setScale(0, RoundingMode.FLOOR)
            val quantity = recommendation.quantity
            val body = OrderRequest(
                orderType = OrderTypeRequest.MARKET,
                session = Session.NORMAL,
                duration = Duration.DAY,
                orderStrategyType = OrderStrategyType.SINGLE,
                orderLegCollection = listOf(
                    OrderLegCollection(
                        instruction = recommendation.recommendation.toInstruction(),
                        quantity = quantity,
                        instrument = TransactionEquity(symbol = recommendation.symbol)
                    )
                ),
            )
            setBody(body)
        }
}

private fun StrategyAction.toInstruction(): Instruction = when (this) {
    StrategyAction.BUY -> Instruction.BUY
    StrategyAction.HOLD -> throw Exception("hold is not a valid instruction")
    StrategyAction.SELL -> Instruction.SELL
}
