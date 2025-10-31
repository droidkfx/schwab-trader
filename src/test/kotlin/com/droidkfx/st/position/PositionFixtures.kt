package com.droidkfx.st.position

import com.droidkfx.st.util.serialization.KBigDecimal
import java.math.BigDecimal

fun defaultPosition(
    symbol: String = "AAPL",
    quantity: KBigDecimal = BigDecimal("10"),
    lastKnownPrice: KBigDecimal = BigDecimal("150.25"),
) = Position(
    symbol = symbol,
    quantity = quantity,
    lastKnownPrice = lastKnownPrice,
)

fun defaultCurrentPositions(
    accountCash: KBigDecimal = BigDecimal("1000.00"),
    positions: List<Position> = listOf(defaultPosition()),
) = CurrentPositions(
    accountCash = accountCash,
    positions = positions,
)

fun defaultPositionTarget(
    symbol: String = "AAPL",
    allocationTarget: KBigDecimal = BigDecimal("0.25"),
) = PositionTarget(
    symbol = symbol,
    allocationTarget = allocationTarget,
)
