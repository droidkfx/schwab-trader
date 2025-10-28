package com.droidkfx.st.orders

import com.droidkfx.st.account.Account
import com.droidkfx.st.schwab.client.OrdersClient
import com.droidkfx.st.strategy.PositionRecommendation

class OrderService(ordersClient: OrdersClient) {
    fun previewOrder(account: Account, recommendation: PositionRecommendation) {
        TODO("Not yet implemented")
    }
}
