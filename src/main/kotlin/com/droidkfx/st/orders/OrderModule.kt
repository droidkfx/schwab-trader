package com.droidkfx.st.orders

import org.koin.core.context.GlobalContext

class OrderModule() {
    val orderService: OrderService = OrderService(GlobalContext.get().get())
}