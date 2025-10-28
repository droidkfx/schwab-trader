package com.droidkfx.st.controller

import com.droidkfx.st.account.AccountService
import com.droidkfx.st.orders.OrderService
import com.droidkfx.st.position.AccountPositionService
import com.droidkfx.st.position.PositionTarget
import com.droidkfx.st.view.AccountTab
import com.droidkfx.st.view.model.AccountTabViewModel
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class AccountTab(
    private val accountPositionService: AccountPositionService,
    private val accountService: AccountService,
    private val orderService: OrderService,
    private val viewModel: AccountTabViewModel,
) : AccountTab(viewModel) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        viewModel.accountNameDataBinding.addListener {
            accountService.saveAccount(viewModel.account)
        }
    }

    override suspend fun saveAccountPositions() {
        logger.debug { "saveAccountPositions" }
        val newTargets = viewModel.data.map {
            PositionTarget(it.symbol, it.allocationTarget)
        }
        val ap = accountPositionService.updateAccountPositionTargets(
            viewModel.accountId,
            newTargets
        )
        viewModel.updateAccountPosition(ap)
        viewModel.rebuildAllocationRows()
    }

    override suspend fun refreshData() {
        logger.debug { "refreshData" }
        val newAccountPosition = accountPositionService.refreshAccountPosition(viewModel.currentAccountPosition())
        viewModel.updateAccountPosition(newAccountPosition)
        viewModel.rebuildAllocationRows()
    }

    override suspend fun processOrders() {
        TODO("Not yet implemented")
    }
}