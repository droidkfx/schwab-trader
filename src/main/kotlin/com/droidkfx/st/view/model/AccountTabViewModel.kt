package com.droidkfx.st.view.model

import com.droidkfx.st.account.Account
import com.droidkfx.st.account.AccountService
import com.droidkfx.st.orders.OrderService
import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.position.AccountPositionService
import com.droidkfx.st.position.PositionTarget
import com.droidkfx.st.strategy.PositionRecommendation
import com.droidkfx.st.strategy.StrategyAction
import com.droidkfx.st.util.databind.ReadWriteListDataBinding
import com.droidkfx.st.util.databind.ValueDataBinding
import com.droidkfx.st.util.databind.toDataBinding
import com.droidkfx.st.util.pmap
import com.droidkfx.st.util.progress.ProgressService
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.coroutineScope
import java.math.BigDecimal

class AccountTabViewModel(
    private var accountPosition: AccountPosition,
    private val accountPositionService: AccountPositionService,
    private val accountService: AccountService,
    private val orderService: OrderService,
    private val progressService: ProgressService,
) {
    private val logger = logger {}

    val data: ReadWriteListDataBinding<AllocationRowViewModel> = accountPosition.toAllocationRows().toDataBinding()
    val accountNameDataBinding: ValueDataBinding<String> = account.name.toDataBinding()
    val accountCash: ValueDataBinding<BigDecimal> = accountPosition.currentCash.toDataBinding()

    init {
        accountNameDataBinding.addListener {
            accountService.saveAccount(account)
        }
    }

    fun setAccountName(name: String) {
        account.name = name
        accountNameDataBinding.value = name
    }

    val account: Account
        get() = accountPosition.account

    val accountId: String
        get() = account.id

    val recommendations: List<PositionRecommendation>
        get() = accountPosition.currentRecommendedChanges

    fun currentAccountPosition(): AccountPosition = accountPosition.copy()

    fun updateAccountPosition(ap: AccountPosition) {
        this.accountPosition = ap
        setAccountName(ap.account.name)
        accountCash.value = ap.currentCash
    }

    fun rebuildAllocationRows() {
        val newRows = accountPosition.toAllocationRows()
        for (row: AllocationRowViewModel in newRows) {
            data.indexOfFirst { it.symbol == row.symbol }.let {
                if (it == -1) {
                    data.add(row)
                } else {
                    data[it] = row
                }
            }
        }
    }

    suspend fun saveAccountPositions() {
        logger.debug { "saveAccountPositions" }
        progressService.track("Saving ${account.name}") {
            val newTargets = data.map { PositionTarget(it.symbol, it.allocationTarget) }
            val ap = accountPositionService.updateAccountPositionTargets(accountId, newTargets)
            updateAccountPosition(ap)
            rebuildAllocationRows()
        }
    }

    suspend fun refreshData() {
        logger.debug { "refreshData" }
        progressService.track("Refreshing ${account.name}") {
            val newAccountPosition = accountPositionService.refreshAccountPosition(currentAccountPosition())
            updateAccountPosition(newAccountPosition)
            rebuildAllocationRows()
        }
    }

    suspend fun processOrders() = coroutineScope {
        logger.debug { "processOrders" }
        progressService.track("Processing orders for ${account.name}") {
            val orderPreviews = recommendations
                .filter { it.recommendation != StrategyAction.HOLD }
                .pmap { orderService.previewOrder(account, it) }
                .toList()
            if (orderPreviews.all { it != null }) {
                recommendations
                    .filter { it.recommendation != StrategyAction.HOLD }
                    .pmap { orderService.order(account, it) }
            }
        }
    }
}
