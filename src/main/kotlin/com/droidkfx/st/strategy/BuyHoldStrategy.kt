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
        var cashToAllocate = BigDecimal.ZERO

        data class AllocationIntermediary(
            val positionProperties: PositionProperties,
            val deltaToNextShare: BigDecimal,
            val recommendation: PositionRecommendation
        )

        val easyBuyAllocations = onlyBuyAllocations.map {
            val weigthedDelta = it.delta.abs() / totalDelta
            val valueToAllocate = weigthedDelta * accountCash
            val sharesToAllocate = (valueToAllocate / it.position.lastKnownPrice).setScale(0, RoundingMode.FLOOR)
            val unallocatedValue = valueToAllocate - (sharesToAllocate * it.position.lastKnownPrice)
            cashToAllocate += unallocatedValue

            AllocationIntermediary(
                it,
                it.position.lastKnownPrice - unallocatedValue,
                PositionRecommendation(
                    it.position.symbol,
                    if (sharesToAllocate == BigDecimal.ZERO) StrategyAction.HOLD else StrategyAction.BUY,
                    sharesToAllocate
                )
            )
        }

        var finalBuyAllocations = easyBuyAllocations
        var changeMade = true
        while (changeMade && cashToAllocate > BigDecimal.ZERO) {
            changeMade = false
            finalBuyAllocations = finalBuyAllocations.sortedBy { it.deltaToNextShare }
                .map {
                    if (it.positionProperties.position.lastKnownPrice < cashToAllocate) {
                        cashToAllocate -= it.positionProperties.position.lastKnownPrice
                        changeMade = true
                        it.copy(
                            deltaToNextShare = it.positionProperties.position.lastKnownPrice,
                            recommendation = it.recommendation.copy(
                                quantity = it.recommendation.quantity + BigDecimal.ONE,
                                recommendation = StrategyAction.BUY
                            )
                        )
                    } else {
                        it
                    }
                }
        }

        return finalBuyAllocations.map { it.recommendation } + holdAllocations.map {
            PositionRecommendation(
                it.position.symbol,
                StrategyAction.HOLD,
                BigDecimal.ZERO
            )
        }
    }
}