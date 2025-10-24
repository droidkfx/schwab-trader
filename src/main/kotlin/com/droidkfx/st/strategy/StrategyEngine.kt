package com.droidkfx.st.strategy

import com.droidkfx.st.position.Position
import com.droidkfx.st.position.PositionTarget


data class PositionRecommendation(val symbol: String, val recommendation: StrategyAction, val quantity: Double)

enum class StrategyAction {
    BUY, SELL, HOLD
}

interface StrategyEngine {
    fun buildRecommendations(
        positions: List<Position>,
        allocationTargets: List<PositionTarget>,
        accountCash: Double
    ): List<PositionRecommendation>
}