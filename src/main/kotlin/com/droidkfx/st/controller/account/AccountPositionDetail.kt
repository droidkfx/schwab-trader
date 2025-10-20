package com.droidkfx.st.controller.account

import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.position.PositionTarget
import com.droidkfx.st.view.account.AccountPositionDetail

internal class AccountPositionDetail(
    acctData: AccountPosition,
    val onSave: (String, List<PositionTarget>) -> Unit
) :
    AccountPositionDetail(acctData) {
    override suspend fun save(
        accountId: String,
        newPositionTargets: List<PositionTarget>
    ) = onSave(accountId, newPositionTargets)
}