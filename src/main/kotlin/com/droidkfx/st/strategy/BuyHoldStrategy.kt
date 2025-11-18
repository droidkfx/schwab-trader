package com.droidkfx.st.strategy

import com.droidkfx.st.position.Position
import com.droidkfx.st.position.PositionTarget
import com.droidkfx.st.schwab.client.QuotesClient
import java.math.BigDecimal
import java.math.RoundingMode

internal class BuyHoldStrategy(
    private val quotesClient: QuotesClient
) : StrategyEngine {

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
        val knownPositions = allocationTargets.map { target ->
            positions.firstOrNull { it.symbol == target.symbol }
                ?.let { PositionProperties(it, target) }
                ?: PositionProperties(Position(target.symbol, BigDecimal.ZERO), target)
        }

        val (unmappedPositions, mappedPositions) = knownPositions.partition { it.position.lastKnownPrice == BigDecimal.ZERO }
        val fetchedQuotes = unmappedPositions.map { it.position.symbol }.let {
            if (it.isEmpty()) emptyMap()
            else quotesClient.getQuotesForSymbols(it).data ?: emptyMap()
        }

        val positionAllocations = mappedPositions + unmappedPositions.mapNotNull {
            it.copy(
                position = it.position.copy(
                    lastKnownPrice = fetchedQuotes[it.position.symbol]?.quote?.lastPrice ?: BigDecimal.ZERO
                )
            ).let { if (it.position.lastKnownPrice == BigDecimal.ZERO) null else it }
        }

        val totalManagedValue = positionAllocations.sumOf { it.position.lastKnownValue } + accountCash
        positionAllocations.forEach {
            // can't use `==` because that requires scale to be equal
            val actualAllocation = if (totalManagedValue.compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal.ZERO
            } else {
                (it.position.lastKnownValue / totalManagedValue) * BigDecimal(100)
            }
            it.delta = (actualAllocation - it.target.allocationTarget).setScale(4)
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
            val valueToAllocate = if (accountCash < BigDecimal.ZERO) BigDecimal.ZERO else weigthedDelta * accountCash
            val sharesToAllocate = (valueToAllocate / it.position.lastKnownPrice).setScale(0, RoundingMode.FLOOR)
            val unallocatedValue = valueToAllocate - (sharesToAllocate * it.position.lastKnownPrice)
            cashToAllocate += unallocatedValue

            AllocationIntermediary(
                it,
                it.position.lastKnownPrice - unallocatedValue,
                PositionRecommendation(
                    it.position.symbol,
                    if (sharesToAllocate == BigDecimal.ZERO) StrategyAction.HOLD else StrategyAction.BUY,
                    sharesToAllocate,
                    it.position.lastKnownPrice
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
                BigDecimal.ZERO,
                it.position.lastKnownPrice
            )
        }
    }
}