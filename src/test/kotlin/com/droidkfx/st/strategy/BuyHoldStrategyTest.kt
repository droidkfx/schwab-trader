package com.droidkfx.st.strategy

import com.droidkfx.st.position.Position
import com.droidkfx.st.position.PositionTarget
import com.droidkfx.st.schwab.client.ApiResponse
import com.droidkfx.st.schwab.client.QuoteResponse
import com.droidkfx.st.schwab.client.QuotesClient
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BuyHoldStrategyTest {

    private fun strategy(quotesClient: QuotesClient = mockk(relaxed = true)) =
        BuyHoldStrategy(quotesClient)

    private fun position(symbol: String, quantity: Double, price: Double) =
        Position(symbol, BigDecimal(quantity.toString()), BigDecimal(price.toString()))

    private fun target(symbol: String, allocation: Double) =
        PositionTarget(symbol, BigDecimal(allocation.toString()))

    // --- Empty / edge cases ---

    @Test
    fun `empty positions and targets returns empty recommendations`() {
        val result = runBlocking {
            strategy().buildRecommendations(emptyList(), emptyList(), BigDecimal.ZERO)
        }
        assertTrue(result.isEmpty())
    }

    @Test
    fun `targets with no matching positions and zero price are excluded`() {
        val mockClient = mockk<QuotesClient>()
        coEvery { mockClient.getQuotesForSymbols(any()) } returns ApiResponse(data = emptyMap())

        val result = runBlocking {
            strategy(mockClient).buildRecommendations(
                positions = emptyList(),
                // Strategy creates Position(symbol, BigDecimal.ZERO) for unmatched targets
                allocationTargets = listOf(target("AAPL", 100.0)),
                accountCash = BigDecimal("500")
            )
        }
        // Position price is BigDecimal.ZERO, and no quote returned → excluded from recommendations
        assertTrue(result.isEmpty(), "Positions with zero price and no quote should be excluded")
    }

    // --- HOLD scenarios ---

    @Test
    fun `position exactly at target allocation generates HOLD`() {
        val result = runBlocking {
            strategy().buildRecommendations(
                positions = listOf(position("AAPL", 10.0, 100.0)),  // value=1000
                allocationTargets = listOf(target("AAPL", 100.0)),   // 100% target
                accountCash = BigDecimal.ZERO                         // no cash → total=1000, actual=100%
            )
        }
        assertEquals(1, result.size)
        val rec = result.first { it.symbol == "AAPL" }
        assertEquals(StrategyAction.HOLD, rec.recommendation)
        assertEquals(0, BigDecimal.ZERO.compareTo(rec.quantity))
    }

    @Test
    fun `position above target allocation generates HOLD`() {
        val result = runBlocking {
            strategy().buildRecommendations(
                positions = listOf(position("AAPL", 10.0, 100.0)),  // value=1000
                allocationTargets = listOf(target("AAPL", 50.0)),    // only 50% target
                accountCash = BigDecimal.ZERO                         // total=1000, actual=100% > 50%
            )
        }
        val rec = result.first { it.symbol == "AAPL" }
        assertEquals(StrategyAction.HOLD, rec.recommendation)
    }

    // --- BUY scenarios ---

    @Test
    fun `single underweight position with cash generates BUY`() {
        val result = runBlocking {
            strategy().buildRecommendations(
                positions = listOf(position("AAPL", 0.0, 100.0)),   // value=0
                allocationTargets = listOf(target("AAPL", 100.0)),   // 100% target
                accountCash = BigDecimal("200")                       // $200 cash, total=$200
            )
        }
        val rec = result.first { it.symbol == "AAPL" }
        assertEquals(StrategyAction.BUY, rec.recommendation)
        assertEquals(0, BigDecimal("2").compareTo(rec.quantity))
    }

    @Test
    fun `BUY quantity does not exceed available cash`() {
        val result = runBlocking {
            strategy().buildRecommendations(
                positions = listOf(position("AAPL", 0.0, 100.0)),
                allocationTargets = listOf(target("AAPL", 100.0)),
                accountCash = BigDecimal("250")  // can only buy 2 shares at $100
            )
        }
        val rec = result.first { it.symbol == "AAPL" }
        assertEquals(StrategyAction.BUY, rec.recommendation)
        assertEquals(0, BigDecimal("2").compareTo(rec.quantity))
    }

    @Test
    fun `two underweight positions receive proportional cash`() {
        val result = runBlocking {
            strategy().buildRecommendations(
                positions = listOf(
                    position("AAPL", 0.0, 100.0),
                    position("MSFT", 0.0, 40.0)
                ),
                allocationTargets = listOf(
                    target("AAPL", 50.0),
                    target("MSFT", 50.0)
                ),
                accountCash = BigDecimal("200")  // $100 each: AAPL=1 share, MSFT=2 shares
            )
        }
        val aapl = result.first { it.symbol == "AAPL" }
        val msft = result.first { it.symbol == "MSFT" }
        assertEquals(StrategyAction.BUY, aapl.recommendation)
        assertEquals(0, BigDecimal("1").compareTo(aapl.quantity))
        assertEquals(StrategyAction.BUY, msft.recommendation)
        assertEquals(0, BigDecimal("2").compareTo(msft.quantity))
    }

    @Test
    fun `mixed positions some hold some buy`() {
        val result = runBlocking {
            strategy().buildRecommendations(
                positions = listOf(
                    position("AAPL", 10.0, 100.0),  // value=1000, 50% of 2000
                    position("MSFT", 5.0, 100.0)    // value=500, 25% of 2000
                ),
                allocationTargets = listOf(
                    target("AAPL", 50.0),            // at target → HOLD
                    target("MSFT", 50.0)             // below target → BUY
                ),
                accountCash = BigDecimal("500")       // total=$2500, AAPL=40%, MSFT=20%
            )
        }
        val aapl = result.first { it.symbol == "AAPL" }
        val msft = result.first { it.symbol == "MSFT" }
        assertEquals(StrategyAction.HOLD, aapl.recommendation)
        assertEquals(StrategyAction.BUY, msft.recommendation)
        assertTrue(msft.quantity > BigDecimal.ZERO)
    }

    @Test
    fun `zero cash results in HOLD for underweight positions`() {
        val result = runBlocking {
            strategy().buildRecommendations(
                positions = listOf(position("AAPL", 5.0, 100.0)),  // value=500
                allocationTargets = listOf(target("AAPL", 100.0)),  // only 50% actual
                accountCash = BigDecimal.ZERO                        // no cash to allocate
            )
        }
        val rec = result.first { it.symbol == "AAPL" }
        assertEquals(StrategyAction.HOLD, rec.recommendation)
        assertEquals(0, BigDecimal.ZERO.compareTo(rec.quantity))
    }

    @Test
    fun `recommendation price matches position lastKnownPrice`() {
        val result = runBlocking {
            strategy().buildRecommendations(
                positions = listOf(position("AAPL", 5.0, 123.45)),
                allocationTargets = listOf(target("AAPL", 50.0)),
                accountCash = BigDecimal("200")
            )
        }
        val rec = result.first { it.symbol == "AAPL" }
        assertEquals(0, BigDecimal("123.45").compareTo(rec.price))
    }

    // --- Quote fetching for unmapped positions ---

    @Test
    fun `fetches quotes for positions with zero price`() {
        val mockClient = mockk<QuotesClient>()
        coEvery { mockClient.getQuotesForSymbols(listOf("TSLA")) } returns ApiResponse(
            data = mapOf("TSLA" to QuoteResponse(QuoteResponse.QuoteData(lastPrice = BigDecimal("200"))))
        )

        val result = runBlocking {
            strategy(mockClient).buildRecommendations(
                // Use BigDecimal.ZERO (scale=0) so == comparison in BuyHoldStrategy identifies unmapped positions
                positions = listOf(Position("TSLA", BigDecimal.ZERO, BigDecimal.ZERO)),
                allocationTargets = listOf(target("TSLA", 100.0)),
                accountCash = BigDecimal("400")
            )
        }

        coVerify { mockClient.getQuotesForSymbols(listOf("TSLA")) }
        val rec = result.first { it.symbol == "TSLA" }
        assertEquals(StrategyAction.BUY, rec.recommendation)
        assertEquals(0, BigDecimal("2").compareTo(rec.quantity))
    }

    @Test
    fun `does not call quotesClient when all positions have known prices`() {
        val mockClient = mockk<QuotesClient>()

        runBlocking {
            strategy(mockClient).buildRecommendations(
                positions = listOf(position("AAPL", 5.0, 100.0)),
                allocationTargets = listOf(target("AAPL", 100.0)),
                accountCash = BigDecimal.ZERO
            )
        }

        coVerify(exactly = 0) { mockClient.getQuotesForSymbols(any()) }
    }

    // --- Leftover cash redistribution ---

    @Test
    fun `leftover cash is redistributed to cheapest position`() {
        // AAPL=$100, MSFT=$40. $150 total.
        // Initial split: $75 for AAPL (0 shares, $75 leftover), $75 for MSFT (1 share, $35 leftover)
        // cashToAllocate=$110. Loop: MSFT $40 < $110 → buy 1 more ($70 left), MSFT $40 < $70 → buy 1 more ($30 left)
        // Final: AAPL=0→HOLD, MSFT=3 shares
        val result = runBlocking {
            strategy().buildRecommendations(
                positions = listOf(
                    position("AAPL", 0.0, 100.0),
                    position("MSFT", 0.0, 40.0)
                ),
                allocationTargets = listOf(
                    target("AAPL", 50.0),
                    target("MSFT", 50.0)
                ),
                accountCash = BigDecimal("150")
            )
        }
        val msft = result.first { it.symbol == "MSFT" }
        assertTrue(
            msft.quantity >= BigDecimal("3"),
            "Expected at least 3 MSFT shares from leftover cash, got ${msft.quantity}"
        )
    }
}
