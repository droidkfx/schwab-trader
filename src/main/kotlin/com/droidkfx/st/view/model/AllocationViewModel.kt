package com.droidkfx.st.view.model

data class AllocationRowViewModel(
    @field:Column(name = "Symbol")
    val symbol: String,

    @field:Column(name = "Target Allocation")
    val allocationTarget: Double,
    @field:Column(name = "Owned")
    val currentShares: Double,
    @field:Column(name = "Price")
    val currentPrice: Double,
    @field:Column(name = "Value")
    val currentValue: Double,
    @field:Column(name = "Allocation")
    val currentAllocation: Double,

    @field:Column(name = "Delta")
    val allocationDelta: Double = currentAllocation - allocationTarget,

    @field:Column(name = "Rec Action")
    val tradeAction: String,
    @field:Column(name = "Rec Shares")
    val tradeShares: Double,
    @field:Column(name = "Expected Cost")
    val expectedCost: Double
)
