package com.droidkfx.st.view.model

import com.droidkfx.st.account.Account
import com.droidkfx.st.orders.CachedOrder
import com.droidkfx.st.orders.OrderService
import com.droidkfx.st.orders.isOpen
import com.droidkfx.st.schwab.client.Status
import com.droidkfx.st.util.databind.ValueDataBinding
import com.droidkfx.st.util.databind.toDataBinding
import com.droidkfx.st.util.progress.ProgressService
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.swing.SwingUtilities

data class OrderFilter(
    val statusFilter: StatusFilter = StatusFilter.OPEN,
    val dateField: DateField = DateField.ENTERED,
    val lookbackDays: Int? = 30,
) {
    enum class DateField { ENTERED, CLOSE }

    enum class StatusFilter(val label: String) {
        ALL("All"),
        OPEN("Open"),
        FILLED("Filled"),
        CANCELED("Canceled"),
        EXPIRED("Expired"),
        REJECTED("Rejected"),
    }
}

class OrdersViewModel(
    val account: Account,
    private val orderService: OrderService,
    private val progressService: ProgressService,
) {
    private val logger = logger {}

    private val allOrders = mutableListOf<CachedOrder>().toDataBinding()

    val displayOrders = mutableListOf<OrderRowViewModel>().toDataBinding()
    val filter = ValueDataBinding(OrderFilter())

    init {
        val cached = orderService.getOrders(account)
        if (cached.isNotEmpty()) {
            allOrders.addAll(cached)
            applyFilter()
        }
        filter.addListener { _, _ -> applyFilter() }
    }

    suspend fun refresh() {
        logger.debug { "refresh orders for account: ${account.id}" }
        progressService.track("Fetching orders for ${account.name}") {
            val fresh = orderService.fetchOrders(account)
            SwingUtilities.invokeLater {
                allOrders.clear()
                allOrders.addAll(fresh)
                applyFilter()
            }
        }
    }

    private fun applyFilter() {
        val filtered = allOrders
            .filter { filterByStatus(it) && filterByDate(it) }
            .map { it.toRowViewModel() }
        displayOrders.clear()
        displayOrders.addAll(filtered)
    }

    private fun filterByStatus(order: CachedOrder): Boolean = when (filter.value.statusFilter) {
        OrderFilter.StatusFilter.ALL -> true
        OrderFilter.StatusFilter.OPEN -> order.status.isOpen()
        OrderFilter.StatusFilter.FILLED -> order.status == Status.FILLED
        OrderFilter.StatusFilter.CANCELED -> order.status == Status.CANCELED
        OrderFilter.StatusFilter.EXPIRED -> order.status == Status.EXPIRED
        OrderFilter.StatusFilter.REJECTED -> order.status == Status.REJECTED
    }

    private fun filterByDate(order: CachedOrder): Boolean {
        val lookbackDays = filter.value.lookbackDays ?: return true
        val cutoff = Instant.now().minus(lookbackDays.toLong(), ChronoUnit.DAYS)
        val dateToCheck = when (filter.value.dateField) {
            OrderFilter.DateField.ENTERED -> order.enteredTime
            OrderFilter.DateField.CLOSE -> order.closeTime
        }
        return dateToCheck?.isAfter(cutoff) ?: false
    }
}
