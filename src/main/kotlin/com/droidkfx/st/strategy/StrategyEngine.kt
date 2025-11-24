package com.droidkfx.st.strategy

import com.droidkfx.st.position.Position
import com.droidkfx.st.position.PositionTarget
import com.droidkfx.st.util.serialization.KBigDecimal
import java.math.BigDecimal


data class PositionRecommendation(
    val symbol: String,
    val recommendation: StrategyAction,
    val quantity: KBigDecimal,
    val price: KBigDecimal
)

enum class StrategyAction {
    BUY, SELL, HOLD
}

interface StrategyEngine {
    suspend fun buildRecommendations(
        positions: List<Position>,
        allocationTargets: List<PositionTarget>,
        accountCash: BigDecimal
    ): List<PositionRecommendation>
}