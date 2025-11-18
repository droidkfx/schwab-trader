package com.droidkfx.st.strategy

import com.droidkfx.st.schwab.client.SchwabClientModule

class StrategyModule(clientModule: SchwabClientModule) {
    val defaultStrategy: StrategyEngine = BuyHoldStrategy(
        clientModule.quotesClient
    )
}