package com.droidkfx.st.controller

import com.droidkfx.st.databind.DataBinding
import com.droidkfx.st.oauth.OauthModule
import com.droidkfx.st.view.model.AccountTabViewModel
import com.droidkfx.st.view.model.AllocationRowViewModel
import com.formdev.flatlaf.FlatDarkLaf
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class ControllerModule(oathModule: OauthModule) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        FlatDarkLaf.setup().also { logger.info { "Dark LaF setup complete" } }
    }

    val menuBarController = MenuBar(oathModule.oauthService)
    val statusBarController = StatusBar(oathModule.oauthService)

    val accounts: DataBinding<List<AccountTabViewModel>?> = DataBinding(
        listOf(
            AccountTabViewModel(
                title = "Account 123456",
                data = listOf(
                    AllocationRowViewModel(
                        tradeAction = "BUY",
                        tradeShares = 5.0,
                        symbol = "CWS",
                        allocationTarget = 14.5,
                        currentShares = 270.0,
                        currentPrice = 67.74,
                        currentAllocation = 13.41
                    ),
                    AllocationRowViewModel(
                        tradeAction = "BUY",
                        tradeShares = 0.0,
                        symbol = "SPY",
                        allocationTarget = 14.5,
                        currentShares = 28.0,
                        currentPrice = 658.73,
                        currentAllocation = 13.52
                    ),
                    AllocationRowViewModel(
                        tradeAction = "BUY",
                        tradeShares = 2.0,
                        symbol = "IUS",
                        allocationTarget = 12.5,
                        currentShares = 304.0,
                        currentPrice = 54.93,
                        currentAllocation = 12.24
                    ),
                    AllocationRowViewModel(
                        tradeAction = "BUY",
                        tradeShares = 0.0,
                        symbol = "IWY",
                        allocationTarget = 12.5,
                        currentShares = 62.0,
                        currentPrice = 269.47,
                        currentAllocation = 12.25
                    ),
                    AllocationRowViewModel(
                        tradeAction = "BUY",
                        tradeShares = 1.0,
                        symbol = "SPGP",
                        allocationTarget = 10.0,
                        currentShares = 117.0,
                        currentPrice = 113.08,
                        currentAllocation = 9.70
                    ),
                    AllocationRowViewModel(
                        tradeAction = "BUY",
                        tradeShares = 1.0,
                        symbol = "TMFC",
                        allocationTarget = 9.0,
                        currentShares = 173.0,
                        currentPrice = 69.57,
                        currentAllocation = 8.82
                    ),
                    AllocationRowViewModel(
                        tradeAction = "BUY",
                        tradeShares = 0.0,
                        symbol = "URTH",
                        allocationTarget = 5.0,
                        currentShares = 37.0,
                        currentPrice = 179.55,
                        currentAllocation = 4.87
                    ),
                    AllocationRowViewModel(
                        tradeAction = "BUY",
                        tradeShares = 1.0,
                        symbol = "SMH",
                        allocationTarget = 7.5,
                        currentShares = 27.0,
                        currentPrice = 320.06,
                        currentAllocation = 6.34
                    ),
                    AllocationRowViewModel(
                        tradeAction = "BUY",
                        tradeShares = 3.0,
                        symbol = "TMFX",
                        allocationTarget = 4.0,
                        currentShares = 243.0,
                        currentPrice = 21.55,
                        currentAllocation = 3.84
                    ),
                    AllocationRowViewModel(
                        tradeAction = "HOLD",
                        tradeShares = 0.0,
                        symbol = "BREFX",
                        allocationTarget = 1.5,
                        currentShares = 85.0,
                        currentPrice = 40.15,
                        currentAllocation = 2.50
                    ),
                    AllocationRowViewModel(
                        tradeAction = "HOLD",
                        tradeShares = 0.0,
                        symbol = "IQM",
                        allocationTarget = 2.5,
                        currentShares = 49.0,
                        currentPrice = 85.59,
                        currentAllocation = 3.07
                    ),
                    AllocationRowViewModel(
                        tradeAction = "BUY",
                        tradeShares = 1.0,
                        symbol = "HSCZ",
                        allocationTarget = 2.5,
                        currentShares = 88.0,
                        currentPrice = 37.45,
                        currentAllocation = 2.42
                    ),
                    AllocationRowViewModel(
                        tradeAction = "HOLD",
                        tradeShares = 0.0,
                        symbol = "LIT",
                        allocationTarget = 1.0,
                        currentShares = 65.0,
                        currentPrice = 55.18,
                        currentAllocation = 2.63
                    ),
                    AllocationRowViewModel(
                        tradeAction = "HOLD",
                        tradeShares = 0.0,
                        symbol = "URNM",
                        allocationTarget = 1.0,
                        currentShares = 56.0,
                        currentPrice = 60.35,
                        currentAllocation = 2.48
                    ),
                    AllocationRowViewModel(
                        tradeAction = "BUY",
                        tradeShares = 0.0,
                        symbol = "VTI",
                        allocationTarget = 2.0,
                        currentShares = 8.0,
                        currentPrice = 325.76,
                        currentAllocation = 1.91
                    ),
                    AllocationRowViewModel(
                        tradeAction = "HOLD",
                        tradeShares = 0.0,
                        symbol = "MNREX",
                        allocationTarget = 0.0,
                        currentShares = 0.0,
                        currentPrice = 0.00,
                        currentAllocation = 0.00
                    ),
                )
            ),
            AccountTabViewModel(
                title = "Account 654321",
                data = listOf()
            )
        )
    )

    val accountTabs = AccountTabs(accounts)
    val mainController = Main(
        statusBarController = statusBarController,
        menuBarController = menuBarController,
        accountTabs = accountTabs,
    )
}