package com.droidkfx.st.view

import com.droidkfx.st.view.setting.SettingsModule
import com.formdev.flatlaf.FlatDarkLaf
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.koin.core.context.GlobalContext

class ViewModule(
) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        FlatDarkLaf.setup().also { logger.info { "Dark LaF setup complete" } }
    }

    private val settingsModule = SettingsModule()

    val main = Main(
        StatusBar(GlobalContext.get().get()),
        MenuBar(GlobalContext.get().get(), settingsModule.settingsDialog),
        AccountTabs(GlobalContext.get().get())
    )
}