package com.droidkfx.st.view.model

import com.droidkfx.st.position.AccountPosition

data class AllocationRowViewModel(
    @field:Column(name = "Symbol", position = 0)
    var symbol: String,

    @field:Column(name = "Target Allocation", mapper = PercentReadTableValueMapper::class, position = 1)
    var allocationTarget: Double,
    @field:Column(name = "Owned", mapper = DoubleReadTableValueMapper::class, position = 2, editable = false)
    var currentShares: Double,
    @field:Column(name = "Price", mapper = DollarReadTableValueMapper::class, position = 3, editable = false)
    var currentPrice: Double,
    @field:Column(name = "Allocation", mapper = PercentReadTableValueMapper::class, position = 5, editable = false)
    var currentAllocation: Double,

    @field:Column(name = "Rec Action", position = 7, editable = false)
    var tradeAction: String,
    @field:Column(name = "Rec Shares", mapper = DoubleReadTableValueMapper::class, position = 8, editable = false)
    var tradeShares: Double,
) {
    val allocationDelta: Double
        @Column(name = "Delta", mapper = PercentReadTableValueMapper::class, position = 6)
        get() = currentAllocation - allocationTarget

    val expectedCost: Double
        @Column(name = "Expected Cost", mapper = DollarReadTableValueMapper::class, position = 9)
        get() = currentPrice * tradeShares * if (tradeAction == "SELL") -1.0 else 1.0

    val currentValue: Double
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
            currentShares = currentPosition?.quantity ?: 0.0,
            currentPrice = currentPosition?.lastKnownPrice ?: 0.0,
            currentAllocation = 0.0,
            tradeAction = currentRecommendation?.recommendation?.name ?: "TBD",
            tradeShares = currentRecommendation?.quantity ?: 0.0
        )
    }.toCollection(rows)
    val totalValue = rows.sumOf { it.currentValue }
    rows.forEach { row ->
        row.currentAllocation = (row.currentValue / totalValue) * 100
    }
    return rows
}