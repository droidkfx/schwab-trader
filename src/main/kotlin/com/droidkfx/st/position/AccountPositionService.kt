package com.droidkfx.st.position

import com.droidkfx.st.account.Account
import java.util.UUID

class AccountPositionService {
    fun getAccountPositions(): List<AccountPosition> {
//        return emptyList()
        return listOf(
            AccountPosition(
                Account(
                    id = UUID.randomUUID().toString(),
                    name = "Account 1",
                    accountNumber = "123456789",
                    accountNumberHash = "D4541250B586296FCCE5DEA4463AE17F",
                ), positionTargets = listOf(
                    PositionTarget("SPY", 0.15),
                    PositionTarget("FOO", 0.15),
                    PositionTarget("FIZ", 0.15),
                    PositionTarget("BUZZ", 0.15),
                    PositionTarget("DONE", 0.40)
                )
            ), AccountPosition(
                Account(
                    id = UUID.randomUUID().toString(),
                    name = "Account 2",
                    accountNumber = "987654321",
                    accountNumberHash = "AE4F46B5D6406A0A9DDDE0547FAD9FE6",
                ), positionTargets = listOf(
                    PositionTarget("SPY", 0.40),
                    PositionTarget("FOO", 0.40),
                )
            )
        )
    }
}