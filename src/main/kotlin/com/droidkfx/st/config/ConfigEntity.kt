package com.droidkfx.st.config

import kotlinx.serialization.Serializable

@Serializable
data class CallbackServerConfig(
    val host: String,
    val port: Int,
    val callbackPath: String,
    val sslCertLocation: String,
    val sslCertPassword: String,
    val sslCertAlias: String,
    val sslCertType: String,
) {
    fun url() = "https://$host:$port$callbackPath"
}

@Serializable
data class SchwabClientConfig(
    val key: String,
    val secret: String,
    val callbackServerConfig: CallbackServerConfig,
)

@Serializable
data class ConfigEntity(
    val schwabClientConfig: SchwabClientConfig,
)