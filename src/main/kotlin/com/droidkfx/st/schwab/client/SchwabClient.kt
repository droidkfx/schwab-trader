package com.droidkfx.st.schwab.client

import com.droidkfx.st.config.SchwabClientConfig
import io.ktor.client.*
import io.ktor.client.engine.java.*

class SchwabClient(val config: SchwabClientConfig) {
    private val client = HttpClient(Java)
    val oathClient = OauthClient(config = config, client = client)
}