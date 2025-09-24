package com.droidkfx.st

import com.droidkfx.st.config.ConfigModule
import com.droidkfx.st.controller.ControllerModule
import com.droidkfx.st.databind.DataBinding
import com.droidkfx.st.oauth.OauthModule
import com.droidkfx.st.schwab.SchwabModule

fun main() {
    val configModule = ConfigModule("application.no-commit.config.json")
    val oauthTokenBinding = DataBinding<String?>(null)
    val schwabModule = SchwabModule(configModule, oauthTokenBinding)
    val oauthModule = OauthModule(configModule, schwabModule, oauthTokenBinding)

    ControllerModule(oauthModule)
}