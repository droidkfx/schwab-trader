package com.droidkfx.st.orders

import com.droidkfx.st.account.Account
import com.droidkfx.st.schwab.client.Order
import com.droidkfx.st.schwab.client.OrdersClient
import com.droidkfx.st.schwab.client.PreviewOrder
import com.droidkfx.st.schwab.client.Status
import com.droidkfx.st.strategy.PositionRecommendation
import com.droidkfx.st.strategy.StrategyAction
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.time.LocalDate
import java.time.ZoneId

class OrderService(private val ordersClient: OrdersClient) {
    private val logger = logger {}

    suspend fun order(account: Account, recommendation: PositionRecommendation) {
        logger.trace { "order for account: $account recommendation: $recommendation" }
        if (recommendation.recommendation == StrategyAction.HOLD) {
            logger.debug { "No order requested" }
        } else {
            logger.debug {
                "submitting order ${recommendation.recommendation} for ${recommendation.symbol} @ ${
                    recommendation.price.setScale(
                        2
                    )
                }"
            }
            val response = ordersClient.order(account, recommendation)
            if (response.error != null) {
                logger.error { "Error placing order: ${response.error}" }
            } else {
                logger.debug { "Order placed successfully" }
            }
        }
    }

    suspend fun previewOrder(account: Account, recommendation: PositionRecommendation): PreviewOrder? {
        logger.trace { "previewOrder for account: $account recommendation: $recommendation" }
        if (recommendation.recommendation == StrategyAction.HOLD) {
            logger.debug { "No order to preview, $recommendation" }
            return null
        } else {
            val previewOrder = ordersClient.previewOrder(account, recommendation)
            logger.trace { "order preview, $previewOrder" }
            return previewOrder.data
        }
    }

    suspend fun getOpenOrders(account: Account): List<Order> {
        val ordersResponse = ordersClient.getAccountOrders(
            account.accountNumberHash,
            LocalDate.now().plusDays(-7).atStartOfDay(ZoneId.systemDefault()).toInstant(),
            LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
        )
        return ordersResponse.data?.filter {
            if (it.status == null) {
                return@filter false
            }
            return@filter when (it.status) {
                Status.CANCELED, Status.FILLED, Status.EXPIRED -> false
                else -> true
            }
        } ?: emptyList()
    }
}
