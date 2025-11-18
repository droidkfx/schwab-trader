package com.droidkfx.st.config

import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class CallbackServerConfig(
    val host: String = "127.0.0.1",
    val port: Int = 41241,
    val callbackPath: String = "",
    val sslCertLocation: String = File(getUsersAppDirPath() + "/creds/localhost.pfx").canonicalPath,
    val sslCertPassword: String = "",
    val sslCertAlias: String = "",
    val sslCertType: String = "PKCS12",
) {
    fun url() = "https://$host:$port$callbackPath"
}

@Serializable
data class SchwabClientConfig(
    val key: String = "key",
    val secret: String = "secret",
    val baseApiUrl: String = "api.schwabapi.com",
    val callbackServerConfig: CallbackServerConfig = CallbackServerConfig(),
)

@Serializable
data class ConfigEntity(
    val schwabConfig: SchwabClientConfig = SchwabClientConfig(),
    val repositoryRoot: String = File(getUsersAppDirPath() + "/data").canonicalPath,
)