package com.droidkfx.st.position

import com.droidkfx.st.account.Account
import com.droidkfx.st.strategy.PositionRecommendation
import java.math.BigDecimal

data class AccountPosition(
    val account: Account,
    val positionTargets: List<PositionTarget>,
    val currentPositions: List<Position>,
    val currentRecommendedChanges: List<PositionRecommendation>,
    val currentCash: BigDecimal,
)

fun AccountPosition.withNewPositionTargets(newPositionTargets: List<PositionTarget>) =
    this.copy(positionTargets = newPositionTargets)