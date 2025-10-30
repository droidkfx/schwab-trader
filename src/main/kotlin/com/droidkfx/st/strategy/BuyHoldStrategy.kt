package com.droidkfx.st.strategy

import com.droidkfx.st.position.Position
import com.droidkfx.st.position.PositionTarget
import java.math.BigDecimal
import java.math.RoundingMode

internal class BuyHoldStrategy : StrategyEngine {

    data class PositionProperties(
        val position: Position,
        val target: PositionTarget,
        var delta: BigDecimal = BigDecimal.ZERO
    )

    override fun buildRecommendations(
        positions: List<Position>,
        allocationTargets: List<PositionTarget>,
        accountCash: BigDecimal
    ): List<PositionRecommendation> {
        val positionAllocations = positions
            .mapNotNull {
                allocationTargets
                    .firstOrNull { target -> target.symbol == it.symbol }
                    ?.let { target -> PositionProperties(it, target) }
            }
        val totalManagedValue = positionAllocations.sumOf { it.position.lastKnownValue } + accountCash
        positionAllocations.forEach {
            val actualAllocation = (it.position.lastKnownValue / totalManagedValue) * BigDecimal(100)
            it.delta = actualAllocation - it.target.allocationTarget
        }

        val (onlyBuyAllocations, holdAllocations) = positionAllocations.partition { it.delta < BigDecimal.ZERO }
        val totalDelta = onlyBuyAllocations.sumOf { it.delta }.abs()
        return onlyBuyAllocations.map {
            val weigthedDelta = it.delta.abs() / totalDelta
            val valueToAllocate = weigthedDelta * accountCash
            val sharesToAllocate = (valueToAllocate / it.position.lastKnownPrice).setScale(0, RoundingMode.FLOOR)

            PositionRecommendation(
                it.position.symbol,
                if (sharesToAllocate == BigDecimal.ZERO) StrategyAction.HOLD else StrategyAction.BUY,
                sharesToAllocate
            )
        } + holdAllocations.map { PositionRecommendation(it.position.symbol, StrategyAction.HOLD, BigDecimal.ZERO) }
    }
}