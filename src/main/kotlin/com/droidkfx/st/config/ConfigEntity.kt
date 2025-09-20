package com.droidkfx.st.config

import kotlinx.serialization.Serializable

@Serializable
data class SchwabClientConfig(
    val key: String,
    val secret: String,
)

@Serializable
data class ConfigEntity(
    val schwabClientConfig: SchwabClientConfig,
)