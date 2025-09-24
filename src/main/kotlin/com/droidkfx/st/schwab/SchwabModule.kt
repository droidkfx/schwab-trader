package com.droidkfx.st.schwab

import com.droidkfx.st.config.ConfigModule
import com.droidkfx.st.databind.DataBinding
import com.droidkfx.st.schwab.client.SchwabClient

class SchwabModule(configModule: ConfigModule, oauthToken: DataBinding<String?>) {
    val client = SchwabClient(
        configModule.configService.getConfig().schwabConfig,
        oauthToken
    )
}