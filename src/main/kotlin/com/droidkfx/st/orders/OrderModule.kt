package com.droidkfx.st.orders

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val orderModule = module {
    singleOf(::OrderService)
}