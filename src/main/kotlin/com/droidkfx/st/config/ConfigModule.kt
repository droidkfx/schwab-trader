package com.droidkfx.st.config

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val configModule = module {
    singleOf(::ConfigRepository) { bind<ConfigRepository>() }
    singleOf(::ConfigService) { bind<ConfigService>() }
}