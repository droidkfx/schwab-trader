@file:OptIn(ExperimentalSerializationApi::class)

package com.droidkfx.st.oauth

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class OauthTokenResponse (
    @JsonNames("expires_in")
    val expiresIn: Long,
    @JsonNames("token_type")
    val tokenType: String,
    @JsonNames("scope")
    val scope: String,
    @JsonNames("refresh_token")
    val refreshToken: String,
    @JsonNames("access_token")
    val accessToken: String,
    @JsonNames("id_token")
    val idToken: String
) {
    private val expiresAt = System.currentTimeMillis() + (expiresIn * 1000)
}