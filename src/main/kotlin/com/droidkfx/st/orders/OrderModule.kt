package com.droidkfx.st.orders

import com.droidkfx.st.schwab.client.SchwabClientModule

class OrderModule(clientModule: SchwabClientModule) {
    val orderService: OrderService = OrderService(clientModule.ordersClient)
}