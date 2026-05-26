package com.droidkfx.st.orders

import com.droidkfx.st.account.defaultAccount
import com.droidkfx.st.schwab.client.ApiResponse
import com.droidkfx.st.schwab.client.Order
import com.droidkfx.st.schwab.client.OrdersClient
import com.droidkfx.st.schwab.client.Status
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertSame

class OrderServiceTest {

    private val ordersClient: OrdersClient = mockk(relaxed = true)
    private val orderRepository: OrderRepository = mockk(relaxed = true)
    private val service = OrderService(ordersClient, orderRepository)

    private val account = defaultAccount(id = "acct-1")

    // --- getOrders ---

    @Test
    fun `getOrders delegates to repository`() {
        val orders = listOf(cached(1L))
        every { orderRepository.loadOrders("acct-1") } returns orders
        assertSame(orders, service.getOrders(account))
    }

    // --- fetchOrders: happy path ---

    @Test
    fun `fetchOrders returns union of existing cache and fresh API results`() = runBlocking {
        every { orderRepository.loadOrders("acct-1") } returns listOf(cached(1L))
        coEvery { ordersClient.getAccountOrders(any(), any(), any(), any(), any()) } returns
            ApiResponse(data = listOf(apiOrder(2L)))

        val result = service.fetchOrders(account)
        assertEquals(2, result.size)
    }

    @Test
    fun `fetchOrders fresh API result overwrites stale cache entry for the same orderId`() = runBlocking {
        every { orderRepository.loadOrders("acct-1") } returns listOf(cached(1L, status = Status.WORKING))
        coEvery { ordersClient.getAccountOrders(any(), any(), any(), any(), any()) } returns
            ApiResponse(data = listOf(apiOrder(1L, status = Status.FILLED)))

        val result = service.fetchOrders(account)

        assertEquals(1, result.size)
        assertEquals(Status.FILLED, result.first().status)
    }

    @Test
    fun `fetchOrders returns orders sorted by enteredTime descending`() = runBlocking {
        val older = Instant.parse("2025-01-01T00:00:00Z")
        val newer = Instant.parse("2025-01-10T00:00:00Z")
        every { orderRepository.loadOrders("acct-1") } returns emptyList()
        coEvery { ordersClient.getAccountOrders(any(), any(), any(), any(), any()) } returns
            ApiResponse(data = listOf(apiOrder(1L, enteredTime = older), apiOrder(2L, enteredTime = newer)))

        val result = service.fetchOrders(account)

        assertEquals(2L, result[0].orderId) // newer first
        assertEquals(1L, result[1].orderId)
    }

    @Test
    fun `fetchOrders persists merged result to repository`() = runBlocking {
        every { orderRepository.loadOrders("acct-1") } returns emptyList()
        coEvery { ordersClient.getAccountOrders(any(), any(), any(), any(), any()) } returns
            ApiResponse(data = listOf(apiOrder(7L)))

        service.fetchOrders(account)

        coVerify { orderRepository.saveOrders("acct-1", any()) }
    }

    // --- fetchOrders: error path ---

    @Test
    fun `fetchOrders returns existing cache when API call fails`() = runBlocking {
        val existing = listOf(cached(1L))
        every { orderRepository.loadOrders("acct-1") } returns existing
        coEvery { ordersClient.getAccountOrders(any(), any(), any(), any(), any()) } returns
            ApiResponse(error = mockk(relaxed = true))

        val result = service.fetchOrders(account)
        assertEquals(existing, result)
    }

    // --- helpers ---

    private fun cached(id: Long, status: Status = Status.WORKING, enteredTime: Instant? = null) =
        CachedOrder(orderId = id, accountId = "acct-1", status = status, symbol = "AAPL", enteredTime = enteredTime)

    private fun apiOrder(id: Long, status: Status = Status.WORKING, enteredTime: Instant? = null) =
        Order(orderId = id, status = status, enteredTime = enteredTime)
}
