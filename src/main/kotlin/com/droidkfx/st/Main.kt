package com.droidkfx.st

import com.droidkfx.st.config.ConfigService
import com.droidkfx.st.controller.Main
import com.droidkfx.st.oauth.OauthService
import com.formdev.flatlaf.FlatLightLaf
import javax.swing.UIManager

fun main() {
    val configService = ConfigService("application.no-commit.config.json")
    val config = configService.getConfig()

    val OauthService = OauthService(config.schwabClientConfig)

//    Main()
}