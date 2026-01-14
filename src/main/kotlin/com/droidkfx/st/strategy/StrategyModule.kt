package com.droidkfx.st.strategy

import org.koin.core.context.GlobalContext

class StrategyModule() {
    val defaultStrategy: StrategyEngine = BuyHoldStrategy(
        GlobalContext.get().get()
    )
}