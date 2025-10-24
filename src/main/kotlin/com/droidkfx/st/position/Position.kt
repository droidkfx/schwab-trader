package com.droidkfx.st.position

import com.droidkfx.st.util.KBigDecimal
import kotlinx.serialization.Serializable

@Serializable
data class CurrentPositions(val accountCash: KBigDecimal, val positions: List<Position>)

@Serializable
data class Position(
    val symbol: String,
    val quantity: KBigDecimal,
    val lastKnownPrice: KBigDecimal = KBigDecimal.ZERO,
) {
    val lastKnownValue: KBigDecimal
        get() = lastKnownPrice * quantity
}