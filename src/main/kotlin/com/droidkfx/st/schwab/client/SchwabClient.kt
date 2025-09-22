package com.droidkfx.st.schwab.client

import com.droidkfx.st.config.SchwabClientConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java

class SchwabClient(val config: SchwabClientConfig) {
    private val client = HttpClient(Java)
    val oathClient = OauthClient(config = config, client = client)
}