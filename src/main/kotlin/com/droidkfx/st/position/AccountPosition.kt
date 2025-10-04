package com.droidkfx.st.position

import com.droidkfx.st.account.Account

data class AccountPosition(val Account: Account, val positionTargets: List<PositionTarget>)