package com.droidkfx.st.oauth

import com.droidkfx.st.config.ConfigModule
import com.droidkfx.st.databind.DataBinding
import com.droidkfx.st.schwab.SchwabModule

class OauthModule(
    configModule: ConfigModule,
    schwabModule: SchwabModule,
    oauthToken: DataBinding<String?>
) {
    val oauthLocalServer = LocalServer(configModule.configService.getConfig().schwabConfig.callbackServerConfig)
    val oauthRepository = OauthRepository()
    val oauthService = OauthService(oauthRepository, schwabModule.client.oathClient, oauthLocalServer, oauthToken)
}