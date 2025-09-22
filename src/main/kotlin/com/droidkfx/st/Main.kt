package com.droidkfx.st

import com.droidkfx.st.config.ConfigService
import com.droidkfx.st.controller.Main
import com.droidkfx.st.oauth.LocalOAuthRedirectServer
import com.droidkfx.st.oauth.OauthClient
import com.droidkfx.st.oauth.OauthRepository
import com.droidkfx.st.oauth.OauthService

fun main() {
    val configService = ConfigService("application.no-commit.config.json")
    val config = configService.getConfig()

    val server = LocalOAuthRedirectServer(config.schwabClientConfig.callbackServerConfig)

    val oathClient = OauthClient(config.schwabClientConfig)

    val oauthRepository = OauthRepository()

    OauthService(repo = oauthRepository, client = oathClient, server = server)

    Main()
}