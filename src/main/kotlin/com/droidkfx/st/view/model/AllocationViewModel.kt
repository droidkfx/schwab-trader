package com.droidkfx.st.view.model

data class AllocationRowViewModel(
    @field:Column(name = "Symbol", position = 0)
    val symbol: String,

    @field:Column(name = "Target Allocation", mapper = PercentTableValueMapper::class, position = 1)
    val allocationTarget: Double,
    @field:Column(name = "Owned", mapper = DoubleTableValueMapper::class, position = 2)
    val currentShares: Double,
    @field:Column(name = "Price", mapper = DollarTableValueMapper::class, position = 3)
    val currentPrice: Double,
    @field:Column(name = "Allocation", mapper = PercentTableValueMapper::class, position = 5)
    val currentAllocation: Double,

    @field:Column(name = "Rec Action", position = 7)
    val tradeAction: String,
    @field:Column(name = "Rec Shares", mapper = DoubleTableValueMapper::class, position = 8)
    val tradeShares: Double,

    ) {
    @field:Column(name = "Delta", mapper = PercentTableValueMapper::class, position = 6)
    val allocationDelta: Double = currentAllocation - allocationTarget

    @field:Column(name = "Expected Cost", mapper = DollarTableValueMapper::class, position = 9)
    val expectedCost: Double = currentPrice * tradeShares * if (tradeAction == "SELL") -1.0 else 1.0

    @field:Column(name = "Value", mapper = DollarTableValueMapper::class, position = 4)
    val currentValue: Double = currentPrice * currentShares
}
