package com.droidkfx.st.config

import com.droidkfx.st.util.databind.ValueDataBinding
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val CONFIG_ENTITY = "configEntity"

val configModule = module {
    singleOf(::ConfigRepository)
    singleOf(::ConfigService)
    single<ValueDataBinding<ConfigEntity>>(named(CONFIG_ENTITY)) { get<ConfigService>().configDataBind }
}
