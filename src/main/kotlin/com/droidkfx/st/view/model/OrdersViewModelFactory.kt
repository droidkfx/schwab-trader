package com.droidkfx.st.view.model

import com.droidkfx.st.account.Account
import com.droidkfx.st.orders.OrderService
import com.droidkfx.st.util.progress.ProgressService

class OrdersViewModelFactory(private val orderService: OrderService, private val progressService: ProgressService) {
    fun create(account: Account): OrdersViewModel = OrdersViewModel(account, orderService, progressService)
}
