package com.droidkfx.st.strategy

import com.droidkfx.st.position.Position
import com.droidkfx.st.position.PositionTarget

internal class BuyHoldStrategy : StrategyEngine {
    override fun buildRecommendations(
        positions: List<Position>,
        allocationTargets: List<PositionTarget>,
        accountCash: Double
    ): List<PositionRecommendation> {
        return emptyList()
    }
}