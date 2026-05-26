package com.droidkfx.st.view.model

import com.droidkfx.st.account.defaultAccount
import com.droidkfx.st.orders.CachedOrder
import com.droidkfx.st.orders.OrderService
import com.droidkfx.st.schwab.client.Status
import com.droidkfx.st.util.progress.ProgressService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

class OrdersViewModelTest {

    private val account = defaultAccount()
    private val orderService: OrderService = mockk(relaxed = true)
    private val progressService = ProgressService()

    /** Builds a VM with the given orders pre-loaded as the cache. */
    private fun vm(cache: List<CachedOrder> = emptyList()): OrdersViewModel {
        every { orderService.getOrders(account) } returns cache
        return OrdersViewModel(account, orderService, progressService)
    }

    private fun order(
        id: Long = 1L,
        status: Status = Status.WORKING,
        enteredTime: Instant? = Instant.now().minus(1, ChronoUnit.DAYS),
        closeTime: Instant? = null,
    ) = CachedOrder(
        orderId = id,
        accountId = account.id,
        status = status,
        symbol = "AAPL",
        enteredTime = enteredTime,
        closeTime = closeTime,
    )

    private val allStatuses = OrderFilter.StatusFilter.entries.toSet()

    // --- init loads from cache ---

    @Test
    fun `init populates displayOrders from cache applying the default filter`() {
        // Default filter: all statuses + last 30 days. Both orders are recent.
        val vm = vm(cache = listOf(order(1L), order(2L)))
        assertEquals(2, vm.displayOrders.size)
    }

    @Test
    fun `init leaves displayOrders empty when cache is empty`() {
        assertEquals(0, vm().displayOrders.size)
    }

    // --- status filter ---

    @Test
    fun `all statuses selected shows every order regardless of status`() {
        val vm = vm(cache = listOf(order(1L, Status.WORKING), order(2L, Status.FILLED), order(3L, Status.CANCELED)))
        vm.filter.value = vm.filter.value.copy(statusStatuses = allStatuses, lookbackDays = null)
        assertEquals(3, vm.displayOrders.size)
    }

    @Test
    fun `OPEN hides terminal-status orders`() {
        val vm = vm(cache = listOf(order(1L, Status.WORKING), order(2L, Status.FILLED), order(3L, Status.QUEUED)))
        vm.filter.value =
            vm.filter.value.copy(statusStatuses = setOf(OrderFilter.StatusFilter.OPEN), lookbackDays = null)
        assertEquals(2, vm.displayOrders.size)
    }

    @Test
    fun `FILLED shows only FILLED orders`() {
        val vm = vm(cache = listOf(order(1L, Status.FILLED), order(2L, Status.WORKING), order(3L, Status.FILLED)))
        vm.filter.value =
            vm.filter.value.copy(statusStatuses = setOf(OrderFilter.StatusFilter.FILLED), lookbackDays = null)
        assertEquals(2, vm.displayOrders.size)
    }

    @Test
    fun `CANCELED shows only CANCELED orders`() {
        val vm = vm(cache = listOf(order(1L, Status.CANCELED), order(2L, Status.WORKING)))
        vm.filter.value =
            vm.filter.value.copy(statusStatuses = setOf(OrderFilter.StatusFilter.CANCELED), lookbackDays = null)
        assertEquals(1, vm.displayOrders.size)
    }

    @Test
    fun `EXPIRED shows only EXPIRED orders`() {
        val vm = vm(cache = listOf(order(1L, Status.EXPIRED), order(2L, Status.FILLED)))
        vm.filter.value =
            vm.filter.value.copy(statusStatuses = setOf(OrderFilter.StatusFilter.EXPIRED), lookbackDays = null)
        assertEquals(1, vm.displayOrders.size)
    }

    @Test
    fun `REJECTED shows only REJECTED orders`() {
        val vm = vm(cache = listOf(order(1L, Status.REJECTED), order(2L, Status.WORKING)))
        vm.filter.value =
            vm.filter.value.copy(statusStatuses = setOf(OrderFilter.StatusFilter.REJECTED), lookbackDays = null)
        assertEquals(1, vm.displayOrders.size)
    }

    @Test
    fun `multiple statuses selected shows orders matching any of them`() {
        val vm = vm(cache = listOf(order(1L, Status.FILLED), order(2L, Status.CANCELED), order(3L, Status.WORKING)))
        vm.filter.value = vm.filter.value.copy(
            statusStatuses = setOf(OrderFilter.StatusFilter.FILLED, OrderFilter.StatusFilter.CANCELED),
            lookbackDays = null,
        )
        assertEquals(2, vm.displayOrders.size)
    }

    @Test
    fun `empty status selection shows no orders`() {
        val vm = vm(cache = listOf(order(1L, Status.WORKING), order(2L, Status.FILLED)))
        vm.filter.value = vm.filter.value.copy(statusStatuses = emptySet(), lookbackDays = null)
        assertEquals(0, vm.displayOrders.size)
    }

    // --- date filter ---

    @Test
    fun `no lookback limit shows all orders regardless of age`() {
        val old = order(1L, enteredTime = Instant.now().minus(200, ChronoUnit.DAYS))
        val recent = order(2L, enteredTime = Instant.now().minus(1, ChronoUnit.DAYS))
        val vm = vm(cache = listOf(old, recent))
        vm.filter.value = vm.filter.value.copy(statusStatuses = allStatuses, lookbackDays = null)
        assertEquals(2, vm.displayOrders.size)
    }

    @Test
    fun `lookback window excludes orders older than the cutoff`() {
        val old = order(1L, enteredTime = Instant.now().minus(60, ChronoUnit.DAYS))
        val recent = order(2L, enteredTime = Instant.now().minus(5, ChronoUnit.DAYS))
        val vm = vm(cache = listOf(old, recent))
        vm.filter.value = vm.filter.value.copy(
            statusStatuses = allStatuses,
            lookbackDays = 30,
            dateField = OrderFilter.DateField.ENTERED,
        )
        assertEquals(1, vm.displayOrders.size)
    }

    @Test
    fun `CLOSE dateField filters by closeTime and excludes orders with no closeTime`() {
        val withClose = order(1L, closeTime = Instant.now().minus(5, ChronoUnit.DAYS))
        val noClose = order(2L, closeTime = null)
        val vm = vm(cache = listOf(withClose, noClose))
        vm.filter.value = vm.filter.value.copy(
            statusStatuses = allStatuses,
            lookbackDays = 30,
            dateField = OrderFilter.DateField.CLOSE,
        )
        assertEquals(1, vm.displayOrders.size)
    }

    // --- reactivity ---

    @Test
    fun `changing filter updates displayOrders synchronously`() {
        val vm = vm(
            cache = listOf(
                order(1L, Status.WORKING),
                order(2L, Status.FILLED),
            ),
        )
        // Default is all statuses, last 30 days — both visible
        assertEquals(2, vm.displayOrders.size)

        vm.filter.value = vm.filter.value.copy(statusStatuses = allStatuses, lookbackDays = null)
        assertEquals(2, vm.displayOrders.size)

        vm.filter.value =
            vm.filter.value.copy(statusStatuses = setOf(OrderFilter.StatusFilter.FILLED), lookbackDays = null)
        assertEquals(1, vm.displayOrders.size)
    }
}
