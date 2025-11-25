package com.droidkfx.st.view.setting

import com.droidkfx.st.config.ConfigModule

class SettingsModule(configModule: ConfigModule) {
    val settingsDialog = SettingsDialog(
        configModule.configService.configDataBind
    )
}