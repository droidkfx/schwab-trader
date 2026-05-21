package com.droidkfx.st.orders

import com.droidkfx.st.schwab.client.Instruction
import com.droidkfx.st.schwab.client.Order
import com.droidkfx.st.schwab.client.OrderLegCollection
import com.droidkfx.st.schwab.client.OrderType
import com.droidkfx.st.schwab.client.Status
import com.droidkfx.st.schwab.client.TransactionEquity
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CachedOrderTest {

    // --- Status.isOpen ---

    @Test
    fun `isOpen returns true for WORKING`() = assertTrue(Status.WORKING.isOpen())

    @Test
    fun `isOpen returns true for QUEUED`() = assertTrue(Status.QUEUED.isOpen())

    @Test
    fun `isOpen returns true for ACCEPTED`() = assertTrue(Status.ACCEPTED.isOpen())

    @Test
    fun `isOpen returns true for PENDING_ACTIVATION`() = assertTrue(Status.PENDING_ACTIVATION.isOpen())

    @Test
    fun `isOpen returns true for AWAITING_PARENT_ORDER`() = assertTrue(Status.AWAITING_PARENT_ORDER.isOpen())

    @Test
    fun `isOpen returns false for CANCELED`() = assertFalse(Status.CANCELED.isOpen())

    @Test
    fun `isOpen returns false for FILLED`() = assertFalse(Status.FILLED.isOpen())

    @Test
    fun `isOpen returns false for EXPIRED`() = assertFalse(Status.EXPIRED.isOpen())

    @Test
    fun `isOpen returns false for REJECTED`() = assertFalse(Status.REJECTED.isOpen())

    @Test
    fun `isOpen returns false for REPLACED`() = assertFalse(Status.REPLACED.isOpen())

    // --- Order.toCachedOrder ---

    @Test
    fun `toCachedOrder returns null when orderId is null`() {
        assertNull(Order(orderId = null, status = Status.WORKING).toCachedOrder("acct"))
    }

    @Test
    fun `toCachedOrder maps core fields from Order`() {
        val entered = Instant.parse("2025-01-15T10:00:00Z")
        val closed = Instant.parse("2025-01-15T11:00:00Z")
        val order = Order(
            orderId = 42L,
            status = Status.FILLED,
            orderType = OrderType.LIMIT,
            quantity = BigDecimal("5"),
            filledQuantity = BigDecimal("5"),
            remainingQuantity = BigDecimal("0"),
            price = BigDecimal("150.00"),
            enteredTime = entered,
            closeTime = closed,
            orderLegCollection = listOf(
                OrderLegCollection(
                    instrument = TransactionEquity(symbol = "AAPL"),
                    instruction = Instruction.BUY,
                )
            ),
        )

        val result = order.toCachedOrder("acct-1")!!

        assertEquals(42L, result.orderId)
        assertEquals("acct-1", result.accountId)
        assertEquals(Status.FILLED, result.status)
        assertEquals("AAPL", result.symbol)
        assertEquals(Instruction.BUY, result.instruction)
        assertEquals(OrderType.LIMIT, result.orderType)
        assertEquals(entered, result.enteredTime)
        assertEquals(closed, result.closeTime)
    }

    @Test
    fun `toCachedOrder uses empty symbol when orderLegCollection is null`() {
        val result = Order(orderId = 1L, status = Status.WORKING).toCachedOrder("acct")!!
        assertEquals("", result.symbol)
    }

    @Test
    fun `toCachedOrder defaults status to UNKNOWN when order status is null`() {
        val result = Order(orderId = 1L, status = null).toCachedOrder("acct")!!
        assertEquals(Status.UNKNOWN, result.status)
    }
}
