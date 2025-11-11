package com.droidkfx.st.view.model

import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.strategy.StrategyAction
import java.math.BigDecimal

@Suppress("unused")
data class AllocationRowViewModel(
    @field:Column(name = "Symbol", position = 0)
    var symbol: String,

    @field:Column(name = "Target Allocation", mapper = PercentReadTableValueMapper::class, position = 1)
    var allocationTarget: BigDecimal,
    @field:Column(name = "Owned", mapper = BigDecimalReadTableValueMapper::class, position = 2, editable = false)
    var currentShares: BigDecimal,
    @field:Column(name = "Price", mapper = DollarReadTableValueMapper::class, position = 3, editable = false)
    var currentPrice: BigDecimal,
    @field:Column(name = "Allocation", mapper = PercentReadTableValueMapper::class, position = 5, editable = false)
    var currentAllocation: BigDecimal,

    @field:Column(name = "Rec Action", position = 7, editable = false)
    var tradeAction: String,
    @field:Column(name = "Rec Shares", mapper = BigDecimalReadTableValueMapper::class, position = 8, editable = false)
    var tradeShares: BigDecimal,
) {
    val allocationDelta: BigDecimal
        @Column(name = "Delta", mapper = PercentReadTableValueMapper::class, position = 6)
        get() = currentAllocation - allocationTarget

    val expectedCost: BigDecimal
        @Column(name = "Expected Cost", mapper = DollarReadTableValueMapper::class, position = 9)
        get() = currentPrice * tradeShares * if (tradeAction == StrategyAction.SELL.name) BigDecimal(-1.0) else BigDecimal(
            1.0
        )

    val currentValue: BigDecimal
        @Column(name = "Value", mapper = DollarReadTableValueMapper::class, position = 4)
        get() = currentPrice * currentShares
}

fun AccountPosition.toAllocationRows(): MutableList<AllocationRowViewModel> {
    val rows = mutableListOf<AllocationRowViewModel>()
    this.positionTargets.map { target ->
        val currentPosition = this.currentPositions.firstOrNull { it.symbol == target.symbol }
        val currentRecommendation = this.currentRecommendedChanges.firstOrNull { it.symbol == target.symbol }
        AllocationRowViewModel(
            symbol = target.symbol,
            allocationTarget = target.allocationTarget,
            currentShares = currentPosition?.quantity ?: BigDecimal.ZERO,
            currentPrice = currentPosition?.lastKnownPrice ?: BigDecimal.ZERO,
            currentAllocation = BigDecimal.ZERO,
            tradeAction = currentRecommendation?.recommendation?.name ?: "TBD",
            tradeShares = currentRecommendation?.quantity ?: BigDecimal.ZERO
        )
    }.toCollection(rows)
    val totalValue = rows.sumOf { it.currentValue }
    rows.forEach { row ->
        row.currentAllocation = (row.currentValue / totalValue) * BigDecimal(100)
    }
    return rows
}