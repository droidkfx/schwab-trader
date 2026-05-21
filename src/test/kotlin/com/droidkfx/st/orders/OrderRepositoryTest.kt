package com.droidkfx.st.orders

import com.droidkfx.st.schwab.client.Status
import com.droidkfx.st.util.databind.toDataBinding
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant

class OrderRepositoryTest {

    private lateinit var tempRoot: Path
    private lateinit var repository: OrderRepository

    @BeforeEach
    fun setUp() {
        tempRoot = Files.createTempDirectory("order-repo-test-")
        repository = OrderRepository(tempRoot.toString().toDataBinding())
    }

    @AfterEach
    fun tearDown() {
        tempRoot.toFile().deleteRecursively()
    }

    private fun sampleOrder(orderId: Long, accountId: String = "acct-1") = CachedOrder(
        orderId = orderId,
        accountId = accountId,
        status = Status.FILLED,
        symbol = "AAPL",
        enteredTime = Instant.parse("2025-01-15T10:00:00Z"),
    )

    @Test
    fun `save then load roundtrip`() {
        val orders = listOf(sampleOrder(1001L), sampleOrder(1002L))

        repository.saveOrders("acct-1", orders)
        val loaded = repository.loadOrders("acct-1")

        assertEquals(2, loaded.size)
        assertEquals(1001L, loaded[0].orderId)
        assertEquals(1002L, loaded[1].orderId)
        assertEquals("AAPL", loaded[0].symbol)
    }

    @Test
    fun `loadOrders returns empty list when no file exists`() {
        val loaded = repository.loadOrders("nonexistent")
        assertTrue(loaded.isEmpty())
    }

    @Test
    fun `clear removes the account file`() {
        repository.saveOrders("acct-1", listOf(sampleOrder(1001L)))
        assertTrue(repository.loadOrders("acct-1").isNotEmpty())

        repository.clear("acct-1")

        assertTrue(repository.loadOrders("acct-1").isEmpty())
    }

    @Test
    fun `separate accounts do not interfere`() {
        repository.saveOrders("acct-1", listOf(sampleOrder(1001L, "acct-1")))
        repository.saveOrders("acct-2", listOf(sampleOrder(2001L, "acct-2"), sampleOrder(2002L, "acct-2")))

        assertEquals(1, repository.loadOrders("acct-1").size)
        assertEquals(2, repository.loadOrders("acct-2").size)

        repository.clear("acct-1")

        assertTrue(repository.loadOrders("acct-1").isEmpty())
        assertEquals(2, repository.loadOrders("acct-2").size)
    }

    @Test
    fun `overwrite replaces all orders for account`() {
        repository.saveOrders("acct-1", listOf(sampleOrder(1001L), sampleOrder(1002L)))
        repository.saveOrders("acct-1", listOf(sampleOrder(9999L)))

        val loaded = repository.loadOrders("acct-1")
        assertEquals(1, loaded.size)
        assertEquals(9999L, loaded[0].orderId)
    }
}
