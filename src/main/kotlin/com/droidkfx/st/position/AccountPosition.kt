package com.droidkfx.st.position

import com.droidkfx.st.account.Account

data class AccountPosition(
    val account: Account,
    val positionTargets: List<PositionTarget>,
    val currentPositions: List<Position>,
    val currentCash: Double,
)

fun AccountPosition.withNewPositionTargets(newPositionTargets: List<PositionTarget>) =
    this.copy(positionTargets = newPositionTargets)