package com.droidkfx.st.controller

import com.droidkfx.st.position.AccountPositionService
import com.droidkfx.st.position.PositionTarget
import com.droidkfx.st.util.databind.ReadWriteListDataBinding
import com.droidkfx.st.view.AccountTab
import com.droidkfx.st.view.model.AllocationRowViewModel
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class AccountTab(
    private val accountPositionService: AccountPositionService,
    private val accountId: String,
    private val allocations: ReadWriteListDataBinding<AllocationRowViewModel>,
) : AccountTab(allocations) {
    private val logger = logger {}

    override suspend fun saveAccountPositions() {
        logger.debug { "saveAccountPositions" }
        accountPositionService.updateAccountPositions(accountId, allocations.map {
            PositionTarget(it.symbol, it.allocationTarget)
        })
    }
}