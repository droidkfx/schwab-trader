package com.droidkfx.st.view.model

import com.droidkfx.st.account.AccountService
import com.droidkfx.st.orders.OrderService
import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.position.AccountPositionService

class AccountTabViewModelFactory(
    private val accountPositionService: AccountPositionService,
    private val accountService: AccountService,
    private val orderService: OrderService,
) {
    fun create(ap: AccountPosition): AccountTabViewModel =
        AccountTabViewModel(ap, accountPositionService, accountService, orderService)
}
