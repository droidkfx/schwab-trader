package com.droidkfx.st.view.model

data class AllocationRowViewModel(
    @field:Column(name = "Symbol")
    val symbol: String,

    @field:Column(name = "Target Allocation", mapper = PercentTableValueMapper::class)
    val allocationTarget: Double,
    @field:Column(name = "Owned", mapper = DoubleTableValueMapper::class)
    val currentShares: Double,
    @field:Column(name = "Price", mapper = DollarTableValueMapper::class)
    val currentPrice: Double,
    @field:Column(name = "Value", mapper = DollarTableValueMapper::class)
    val currentValue: Double,
    @field:Column(name = "Allocation", mapper = PercentTableValueMapper::class)
    val currentAllocation: Double,

    @field:Column(name = "Delta", mapper = PercentTableValueMapper::class)
    val allocationDelta: Double = currentAllocation - allocationTarget,

    @field:Column(name = "Rec Action")
    val tradeAction: String,
    @field:Column(name = "Rec Shares", mapper = DoubleTableValueMapper::class)
    val tradeShares: Double,
    @field:Column(name = "Expected Cost", mapper = DollarTableValueMapper::class)
    val expectedCost: Double
)
