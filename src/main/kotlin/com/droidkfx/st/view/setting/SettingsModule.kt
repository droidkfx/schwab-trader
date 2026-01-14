package com.droidkfx.st.view.setting

import com.droidkfx.st.config.CONFIG_ENTITY
import com.droidkfx.st.config.ConfigEntity
import com.droidkfx.st.util.databind.ValueDataBinding
import org.koin.core.qualifier.named
import org.koin.dsl.module

val settingsModule = module {
    single { SettingsDialog(get<ValueDataBinding<ConfigEntity>>(named(CONFIG_ENTITY))) }
}