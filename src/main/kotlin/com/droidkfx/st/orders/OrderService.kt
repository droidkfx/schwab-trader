package com.droidkfx.st.orders

import com.droidkfx.st.account.Account
import com.droidkfx.st.schwab.client.OrdersClient
import com.droidkfx.st.schwab.client.PreviewOrder
import com.droidkfx.st.strategy.PositionRecommendation

class OrderService(private val ordersClient: OrdersClient) {
    fun previewOrder(account: Account, recommendation: PositionRecommendation): PreviewOrder? {
        return ordersClient.previewOrder(account, recommendation).data
    }
}
