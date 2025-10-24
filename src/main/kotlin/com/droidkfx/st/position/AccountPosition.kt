package com.droidkfx.st.position

import com.droidkfx.st.account.Account
import com.droidkfx.st.strategy.PositionRecommendation

data class AccountPosition(
    val account: Account,
    val positionTargets: List<PositionTarget>,
    val currentPositions: List<Position>,
    val currentRecommendedChanges: List<PositionRecommendation>,
    val currentCash: Double,
)

fun AccountPosition.withNewPositionTargets(newPositionTargets: List<PositionTarget>) =
    this.copy(positionTargets = newPositionTargets)