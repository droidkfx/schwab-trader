package com.droidkfx.st.view

import com.droidkfx.st.view.setting.settingsModule
import com.formdev.flatlaf.FlatDarkLaf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

private val viewInternalModule = module {
    FlatDarkLaf.setup()
    includes(settingsModule)
    singleOf(::StatusBar)
    singleOf(::MenuBar)
    singleOf(::AccountTabs)
}

val viewModule = module {
    includes(viewInternalModule)
    singleOf(::Main)
}