package com.droidkfx.st.strategy

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val strategyModule = module {
    singleOf(::BuyHoldStrategy) { bind<StrategyEngine>() }
}