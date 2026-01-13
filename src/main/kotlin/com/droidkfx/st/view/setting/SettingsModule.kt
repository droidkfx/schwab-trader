package com.droidkfx.st.view.setting

import com.droidkfx.st.config.ConfigService
import org.koin.core.context.GlobalContext

class SettingsModule() {
    private val configService: ConfigService by GlobalContext.get().inject()

    val settingsDialog = SettingsDialog(
        configService.configDataBind
    )
}