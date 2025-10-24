package com.droidkfx.st.strategy

import com.droidkfx.st.position.Position
import com.droidkfx.st.position.PositionTarget
import kotlin.math.absoluteValue

internal class BuyHoldStrategy : StrategyEngine {

    data class PositionProperties(val position: Position, val target: PositionTarget, var delta: Double = 0.0)

    override fun buildRecommendations(
        positions: List<Position>,
        allocationTargets: List<PositionTarget>,
        accountCash: Double
    ): List<PositionRecommendation> {
        val positionAllocations = positions
            .mapNotNull {
                allocationTargets
                    .firstOrNull { target -> target.symbol == it.symbol }
                    ?.let { target -> PositionProperties(it, target) }
            }
        val totalManagedValue = positionAllocations.sumOf { it.position.lastKnownValue } + accountCash
        positionAllocations.forEach {
            val actualAllocation = (it.position.lastKnownValue / totalManagedValue) * 100
            it.delta = actualAllocation - it.target.allocationTarget
        }

        val (onlyBuyAllocations, holdAllocations) = positionAllocations.partition { it.delta < 0 }
        val totalDelta = onlyBuyAllocations.sumOf { it.delta }.absoluteValue
        return onlyBuyAllocations.map {
            val weigthedDelta = it.delta.absoluteValue / totalDelta
            val valueToAllocate = weigthedDelta * accountCash
            val sharesToAllocate = valueToAllocate / it.position.lastKnownPrice

            PositionRecommendation(
                it.position.symbol,
                StrategyAction.BUY,
                sharesToAllocate
            )
        } + holdAllocations.map { PositionRecommendation(it.position.symbol, StrategyAction.HOLD, 0.0) }
    }
}