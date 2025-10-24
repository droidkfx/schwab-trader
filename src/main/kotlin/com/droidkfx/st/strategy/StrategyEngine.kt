package com.droidkfx.st.strategy

import com.droidkfx.st.position.Position
import com.droidkfx.st.position.PositionTarget
import com.droidkfx.st.util.KBigDecimal
import java.math.BigDecimal


data class PositionRecommendation(val symbol: String, val recommendation: StrategyAction, val quantity: KBigDecimal)

enum class StrategyAction {
    BUY, SELL, HOLD
}

interface StrategyEngine {
    fun buildRecommendations(
        positions: List<Position>,
        allocationTargets: List<PositionTarget>,
        accountCash: BigDecimal
    ): List<PositionRecommendation>
}