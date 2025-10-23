package com.droidkfx.st.controller

import com.droidkfx.st.account.AccountService
import com.droidkfx.st.position.AccountPositionService
import com.droidkfx.st.position.PositionTarget
import com.droidkfx.st.view.AccountTab
import com.droidkfx.st.view.model.AccountTabViewModel
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class AccountTab(
    private val accountPositionService: AccountPositionService,
    private val accountService: AccountService,
    private val accountTabViewModel: AccountTabViewModel,
) : AccountTab(accountTabViewModel) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        accountTabViewModel.accountNameDataBinding.addListener {
            accountService.saveAccount(accountTabViewModel.account)
        }
    }

    override suspend fun saveAccountPositions() {
        logger.debug { "saveAccountPositions" }
        accountPositionService.updateAccountPositions(
            accountTabViewModel.account.id,
            accountTabViewModel.data.map {
                PositionTarget(it.symbol, it.allocationTarget)
            })
    }

    override suspend fun refreshData() {
        TODO("Not yet implemented")
    }

    override suspend fun processOrders() {
        TODO("Not yet implemented")
    }
}