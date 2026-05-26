package com.droidkfx.st.view.model

import com.droidkfx.st.orders.CachedOrder
import com.droidkfx.st.schwab.client.Instruction
import com.droidkfx.st.schwab.client.OrderType
import com.droidkfx.st.schwab.client.Status
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals

class OrderRowViewModelTest {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault())

    @Test
    fun `toRowViewModel maps all populated fields`() {
        val entered = Instant.parse("2025-03-15T14:30:00Z")
        val closed = Instant.parse("2025-03-15T15:00:00Z")
        val order = CachedOrder(
            orderId = 99L,
            accountId = "acct-1",
            status = Status.FILLED,
            symbol = "MSFT",
            instruction = Instruction.SELL,
            orderType = OrderType.LIMIT,
            quantity = BigDecimal("10"),
            filledQuantity = BigDecimal("10"),
            remainingQuantity = BigDecimal("0"),
            price = BigDecimal("400.00"),
            enteredTime = entered,
            closeTime = closed,
        )

        val row = order.toRowViewModel()

        assertEquals("MSFT", row.symbol)
        assertEquals("SELL", row.action)
        assertEquals("LIMIT", row.type)
        assertEquals("FILLED", row.status)
        assertEquals(BigDecimal("10"), row.quantity)
        assertEquals(BigDecimal("10"), row.filledQuantity)
        assertEquals(BigDecimal("0"), row.remainingQuantity)
        assertEquals(BigDecimal("400.00"), row.price)
        assertEquals(formatter.format(entered), row.enteredTime)
        assertEquals(formatter.format(closed), row.closedTime)
    }

    @Test
    fun `toRowViewModel uses empty string for null instruction and orderType`() {
        val order = CachedOrder(
            orderId = 1L,
            accountId = "acct",
            status = Status.WORKING,
            symbol = "AAPL",
            instruction = null,
            orderType = null,
        )
        val row = order.toRowViewModel()
        assertEquals("", row.action)
        assertEquals("", row.type)
    }

    @Test
    fun `toRowViewModel uses BigDecimal zero for null quantities and price`() {
        val order = CachedOrder(
            orderId = 1L,
            accountId = "acct",
            status = Status.WORKING,
            symbol = "AAPL",
            quantity = null,
            filledQuantity = null,
            remainingQuantity = null,
            price = null,
        )
        val row = order.toRowViewModel()
        assertEquals(BigDecimal.ZERO, row.quantity)
        assertEquals(BigDecimal.ZERO, row.filledQuantity)
        assertEquals(BigDecimal.ZERO, row.remainingQuantity)
        assertEquals(BigDecimal.ZERO, row.price)
    }

    @Test
    fun `toRowViewModel uses empty string for null timestamps`() {
        val order = CachedOrder(
            orderId = 1L,
            accountId = "acct",
            status = Status.WORKING,
            symbol = "AAPL",
            enteredTime = null,
            closeTime = null,
        )
        val row = order.toRowViewModel()
        assertEquals("", row.enteredTime)
        assertEquals("", row.closedTime)
    }
}
