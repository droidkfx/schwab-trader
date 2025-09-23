package com.droidkfx.st.schwab

import com.droidkfx.st.config.ConfigModule
import com.droidkfx.st.schwab.client.SchwabClient

class SchwabModule(configModule: ConfigModule) {
    val client = SchwabClient(configModule.configService.getConfig().schwabConfig)
}