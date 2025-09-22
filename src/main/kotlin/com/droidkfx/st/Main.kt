package com.droidkfx.st

import com.droidkfx.st.config.ConfigService
import com.droidkfx.st.controller.Main
import com.droidkfx.st.oauth.LocalOAuthRedirectServer
import com.droidkfx.st.oauth.OauthRepository
import com.droidkfx.st.oauth.OauthService
import com.droidkfx.st.schwab.client.SchwabClient

fun main() {
    val configService = ConfigService("application.no-commit.config.json")
    val config = configService.getConfig()

    val server = LocalOAuthRedirectServer(config.schwabClientConfig.callbackServerConfig)

    val schwabClient = SchwabClient(config.schwabClientConfig)

    val oauthRepository = OauthRepository()

    OauthService(repo = oauthRepository, client = schwabClient.oathClient, server = server)

    Main()
}