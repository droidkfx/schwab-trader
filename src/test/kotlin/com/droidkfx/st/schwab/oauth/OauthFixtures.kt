package com.droidkfx.st.schwab.oauth

import com.droidkfx.st.schwab.client.OauthTokenResponse

fun defaultOauthTokenResponse(
    expiresIn: Long = 3600,
    tokenType: String = "Bearer",
    scope: String = "openid",
    refreshToken: String = "refresh-token",
    accessToken: String = "access-token",
    // the middle segment must be standard base64 decodable JSON with an "exp" claim
    idToken: String = "header.eyJleHAiOjIwMDAwMDAwMH0.signature",
): OauthTokenResponse = OauthTokenResponse(
    expiresIn = expiresIn,
    tokenType = tokenType,
    scope = scope,
    refreshToken = refreshToken,
    accessToken = accessToken,
    idToken = idToken,
)
