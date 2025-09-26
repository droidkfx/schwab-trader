package com.droidkfx.st.view.model

data class AllocationRowViewModel(
    @field:Column(name = "Symbol", position = 0)
    val symbol: String,

    @field:Column(name = "Target Allocation", position = 1)
    val allocationTarget: Double,
    @field:Column(name = "Owned", position = 2)
    val currentShares: Double,
    @field:Column(name = "Price", position = 3)
    val currentPrice: Double,
    @field:Column(name = "Value", position = 4)
    val currentValue: Double,
    @field:Column(name = "Allocation", position = 5)
    val currentAllocation: Double,

    @field:Column(name = "Rec Action", position = 6)
    val tradeAction: String,
    @field:Column(name = "Rec Shares", position = 7)
    val tradeShares: Double,
    @field:Column(name = "Expected Cost", position = 8)
    val expectedCost: Double
)
