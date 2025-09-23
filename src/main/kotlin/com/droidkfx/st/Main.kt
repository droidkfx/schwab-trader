package com.droidkfx.st

import com.droidkfx.st.config.ConfigModule
import com.droidkfx.st.controller.ControllerModule
import com.droidkfx.st.oauth.OauthModule
import com.droidkfx.st.schwab.SchwabModule
import com.formdev.flatlaf.FlatLightLaf

fun main() {
    FlatLightLaf.setup()

    val configModule = ConfigModule("application.no-commit.config.json")
    val schwabModule = SchwabModule(configModule)
    val oauthModule = OauthModule(configModule, schwabModule)
    val controllerModule = ControllerModule()
}