package com.droidkfx.st.position

import kotlinx.serialization.Serializable

@Serializable
data class CurrentPositions(val accountCash: Double, val positions: List<Position>)

@Serializable
data class Position(
    val symbol: String,
    val quantity: Double,
    val lastKnownPrice: Double = 0.0,
) {
    val lastKnownValue: Double
        get() = lastKnownPrice * quantity
}