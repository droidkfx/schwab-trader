package com.droidkfx.st.config

fun defaultCallbackServerConfig(
    host: String = "localhost",
    port: Int = 0,
    callbackPath: String = "/callback",
    sslCertLocation: String = "none",
    sslCertPassword: String = "",
    sslCertAlias: String = "",
    sslCertType: String = ""
) = CallbackServerConfig(
    host = host,
    port = port,
    callbackPath = callbackPath,
    sslCertLocation = sslCertLocation,
    sslCertPassword = sslCertPassword,
    sslCertAlias = sslCertAlias,
    sslCertType = sslCertType
)
