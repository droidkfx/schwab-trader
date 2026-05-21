package com.droidkfx.st.orders

import com.droidkfx.st.config.CONFIG_ENTITY
import com.droidkfx.st.config.ConfigEntity
import com.droidkfx.st.util.databind.ValueDataBinding
import com.droidkfx.st.util.databind.readOnlyMapped
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val orderModule = module {
    single { OrderRepository(get<ValueDataBinding<ConfigEntity>>(named(CONFIG_ENTITY)).readOnlyMapped { it.repositoryRoot }) }
    singleOf(::OrderService)
}