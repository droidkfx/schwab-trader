package com.droidkfx.st.controller

import com.droidkfx.st.account.AccountService
import com.droidkfx.st.position.AccountPositionService
import com.droidkfx.st.position.PositionTarget
import com.droidkfx.st.view.AccountTab
import com.droidkfx.st.view.model.AccountTabViewModel
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.math.BigDecimal

class AccountTab(
    private val accountPositionService: AccountPositionService,
    private val accountService: AccountService,
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
        accountPositionService.updateAccountPositionTargets(
            viewModel.account.id,
            viewModel.data.map {
                PositionTarget(it.symbol, it.allocationTarget)
            })
    }

    override suspend fun refreshData() {
        logger.debug { "refreshData" }
        val currentPositions = accountPositionService.refreshAccountPositions(viewModel.account)
        viewModel.data.forEach { row ->
            currentPositions.positions.firstOrNull {
                it.symbol == row.symbol
            }?.apply {
                row.currentShares = quantity
                row.currentPrice = lastKnownPrice
            }
        }
        viewModel.accountCash.value = currentPositions.accountCash

        val totalAllocation = viewModel.data.sumOf { it.currentValue } + viewModel.accountCash.value
        viewModel.data.forEach {
            it.currentAllocation = (it.currentValue / totalAllocation) * BigDecimal(100)
            if (it.allocationDelta < BigDecimal.ZERO) {
                it.tradeAction = "BUY"
            } else {
                it.tradeAction = "HOLD"
            }
        }
    }

    override suspend fun processOrders() {
        TODO("Not yet implemented")
    }
}