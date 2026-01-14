package com.droidkfx.st.transaction

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val transactionModule = module {
    singleOf(::TransactionService)
}