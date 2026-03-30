package com.droidkfx.st.view.model

import com.droidkfx.st.account.AccountService
import com.droidkfx.st.orders.OrderService
import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.position.AccountPositionService
import com.droidkfx.st.util.progress.ProgressService

class AccountTabViewModelFactory(
    private val accountPositionService: AccountPositionService,
    private val accountService: AccountService,
    private val orderService: OrderService,
    private val progressService: ProgressService,
) {
    fun create(ap: AccountPosition): AccountTabViewModel =
        AccountTabViewModel(ap, accountPositionService, accountService, orderService, progressService)
}
