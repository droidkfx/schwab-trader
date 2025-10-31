package com.droidkfx.st.config

fun defaultSchwabClientConfig(
    key: String = "k",
    secret: String = "s",
    baseApiUrl: String = "https://example",
    callbackServerConfig: CallbackServerConfig = defaultCallbackServerConfig()
) = SchwabClientConfig(
    key = key,
    secret = secret,
    baseApiUrl = baseApiUrl,
    callbackServerConfig = callbackServerConfig,
)
