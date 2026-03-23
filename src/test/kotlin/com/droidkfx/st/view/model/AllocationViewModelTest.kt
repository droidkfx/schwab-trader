package com.droidkfx.st.view.model

import com.droidkfx.st.account.defaultAccount
import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.position.Position
import com.droidkfx.st.position.PositionTarget
import com.droidkfx.st.strategy.PositionRecommendation
import com.droidkfx.st.strategy.StrategyAction
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AllocationViewModelTest {

    private fun accountPosition(
        positionTargets: List<PositionTarget> = emptyList(),
        currentPositions: List<Position> = emptyList(),
        currentRecommendedChanges: List<PositionRecommendation> = emptyList(),
        currentCash: BigDecimal = BigDecimal.ZERO
    ) = AccountPosition(defaultAccount(), positionTargets, currentPositions, currentRecommendedChanges, currentCash)

    // --- toAllocationRows ---

    @Test
    fun `toAllocationRows returns empty list when no targets`() {
        val rows = accountPosition().toAllocationRows()
        assertTrue(rows.isEmpty())
    }

    @Test
    fun `toAllocationRows creates one row per target`() {
        val pos = accountPosition(
            positionTargets = listOf(
                PositionTarget("AAPL", BigDecimal("50")),
                PositionTarget("MSFT", BigDecimal("50"))
            )
        )
        assertEquals(2, pos.toAllocationRows().size)
    }

    @Test
    fun `toAllocationRows maps symbol and allocationTarget from target`() {
        val pos = accountPosition(
            positionTargets = listOf(PositionTarget("AAPL", BigDecimal("75"))),
            currentPositions = listOf(Position("AAPL", BigDecimal("10"), BigDecimal("150")))
        )
        val row = pos.toAllocationRows()[0]
        assertEquals("AAPL", row.symbol)
        assertEquals(BigDecimal("75"), row.allocationTarget)
    }

    @Test
    fun `toAllocationRows maps shares and price from current position`() {
        val pos = accountPosition(
            positionTargets = listOf(PositionTarget("AAPL", BigDecimal("100"))),
            currentPositions = listOf(Position("AAPL", BigDecimal("10"), BigDecimal("150")))
        )
        val row = pos.toAllocationRows()[0]
        assertEquals(BigDecimal("10"), row.currentShares)
        assertEquals(BigDecimal("150"), row.currentPrice)
    }

    @Test
    fun `toAllocationRows uses zero shares when position is missing`() {
        val pos = accountPosition(
            positionTargets = listOf(PositionTarget("TSLA", BigDecimal("30")))
        )
        val row = pos.toAllocationRows()[0]
        assertEquals(BigDecimal.ZERO, row.currentShares)
    }

    @Test
    fun `toAllocationRows uses recommendation price when position is missing`() {
        val pos = accountPosition(
            positionTargets = listOf(PositionTarget("TSLA", BigDecimal("30"))),
            currentRecommendedChanges = listOf(
                PositionRecommendation("TSLA", StrategyAction.BUY, BigDecimal("5"), BigDecimal("200"))
            )
        )
        val row = pos.toAllocationRows()[0]
        assertEquals(BigDecimal("200"), row.currentPrice)
    }

    @Test
    fun `toAllocationRows calculates equal allocation for equal value positions`() {
        // Use decimal prices so BigDecimal division produces non-integer scale results
        val pos = accountPosition(
            positionTargets = listOf(
                PositionTarget("AAPL", BigDecimal("50")),
                PositionTarget("MSFT", BigDecimal("50"))
            ),
            currentPositions = listOf(
                Position("AAPL", BigDecimal("10"), BigDecimal("100.00")),
                Position("MSFT", BigDecimal("10"), BigDecimal("100.00"))
            )
        )
        val rows = pos.toAllocationRows()
        // Equal value positions should have equal allocation
        assertEquals(0, rows[0].currentAllocation.compareTo(rows[1].currentAllocation))
        // Both allocations should be positive (non-zero value)
        assertTrue(rows[0].currentAllocation > BigDecimal.ZERO)
    }

    @Test
    fun `toAllocationRows sets currentAllocation to zero when total value is zero`() {
        val pos = accountPosition(
            positionTargets = listOf(PositionTarget("AAPL", BigDecimal("100"))),
            currentPositions = listOf(Position("AAPL", BigDecimal("0"), BigDecimal("0")))
        )
        assertEquals(0, BigDecimal.ZERO.compareTo(pos.toAllocationRows()[0].currentAllocation))
    }

    @Test
    fun `toAllocationRows maps recommendation action and shares`() {
        val pos = accountPosition(
            positionTargets = listOf(PositionTarget("AAPL", BigDecimal("50"))),
            currentRecommendedChanges = listOf(
                PositionRecommendation("AAPL", StrategyAction.BUY, BigDecimal("5"), BigDecimal("150"))
            )
        )
        val row = pos.toAllocationRows()[0]
        assertEquals("BUY", row.tradeAction)
        assertEquals(BigDecimal("5"), row.tradeShares)
    }

    @Test
    fun `toAllocationRows sets tradeAction to TBD when no recommendation`() {
        val pos = accountPosition(
            positionTargets = listOf(PositionTarget("AAPL", BigDecimal("50")))
        )
        assertEquals("TBD", pos.toAllocationRows()[0].tradeAction)
    }

    @Test
    fun `toAllocationRows gives higher allocation to higher value position`() {
        // Use decimal prices to ensure non-integer BigDecimal division results
        val pos = accountPosition(
            positionTargets = listOf(
                PositionTarget("AAPL", BigDecimal("50")),
                PositionTarget("MSFT", BigDecimal("50"))
            ),
            currentPositions = listOf(
                Position("AAPL", BigDecimal("10"), BigDecimal("100.00")),  // value=1000
                Position("MSFT", BigDecimal("10"), BigDecimal("300.00"))   // value=3000
            )
        )
        val rows = pos.toAllocationRows()
        // MSFT has 3x the value of AAPL, so MSFT allocation should be higher
        assertTrue(
            rows[1].currentAllocation > rows[0].currentAllocation,
            "MSFT (higher value) should have higher allocation than AAPL"
        )
        // AAPL is 25% and MSFT is 75% — verify the ratio is approximately 1:3
        val ratio = rows[1].currentAllocation.toDouble() / rows[0].currentAllocation.toDouble()
        assertEquals(3.0, ratio, 0.01)
    }

    // --- AllocationRowViewModel computed properties ---

    @Test
    fun `allocationDelta is currentAllocation minus allocationTarget`() {
        val row = AllocationRowViewModel(
            symbol = "AAPL",
            allocationTarget = BigDecimal("50"),
            currentShares = BigDecimal.ZERO,
            currentPrice = BigDecimal.ZERO,
            currentAllocation = BigDecimal("60"),
            tradeAction = "HOLD",
            tradeShares = BigDecimal.ZERO
        )
        assertEquals(0, BigDecimal("10").compareTo(row.allocationDelta))
    }

    @Test
    fun `currentValue is currentPrice times currentShares`() {
        val row = AllocationRowViewModel(
            symbol = "AAPL",
            allocationTarget = BigDecimal("50"),
            currentShares = BigDecimal("10"),
            currentPrice = BigDecimal("150"),
            currentAllocation = BigDecimal.ZERO,
            tradeAction = "HOLD",
            tradeShares = BigDecimal.ZERO
        )
        assertEquals(0, BigDecimal("1500").compareTo(row.currentValue))
    }

    @Test
    fun `expectedCost is positive for BUY`() {
        val row = AllocationRowViewModel(
            symbol = "AAPL",
            allocationTarget = BigDecimal("50"),
            currentShares = BigDecimal.ZERO,
            currentPrice = BigDecimal("100"),
            currentAllocation = BigDecimal.ZERO,
            tradeAction = "BUY",
            tradeShares = BigDecimal("5")
        )
        assertTrue(row.expectedCost > BigDecimal.ZERO, "BUY expectedCost should be positive")
        assertEquals(0, BigDecimal("500").compareTo(row.expectedCost))
    }

    @Test
    fun `expectedCost is negative for SELL`() {
        val row = AllocationRowViewModel(
            symbol = "AAPL",
            allocationTarget = BigDecimal("50"),
            currentShares = BigDecimal("10"),
            currentPrice = BigDecimal("100"),
            currentAllocation = BigDecimal.ZERO,
            tradeAction = "SELL",
            tradeShares = BigDecimal("5")
        )
        assertTrue(row.expectedCost < BigDecimal.ZERO, "SELL expectedCost should be negative")
    }

    @Test
    fun `expectedCost is zero when tradeShares is zero`() {
        val row = AllocationRowViewModel(
            symbol = "AAPL",
            allocationTarget = BigDecimal("50"),
            currentShares = BigDecimal("10"),
            currentPrice = BigDecimal("100"),
            currentAllocation = BigDecimal.ZERO,
            tradeAction = "HOLD",
            tradeShares = BigDecimal.ZERO
        )
        assertEquals(0, BigDecimal.ZERO.compareTo(row.expectedCost))
    }
}
