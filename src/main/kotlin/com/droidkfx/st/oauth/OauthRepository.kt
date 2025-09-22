package com.droidkfx.st.oauth

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File

class OauthRepository {
    @OptIn(ExperimentalSerializationApi::class)
    fun loadExistingToken(): OauthTokenResponse? {
        val tokenFile = File("./creds/oauth-token.json")
        if (tokenFile.exists()) {
            return Json.decodeFromStream<OauthTokenResponse>(tokenFile.inputStream())
        }
        return null
    }

    fun saveToken(token: OauthTokenResponse) {
        val tokenFile = File("./creds/oauth-token.json")
        tokenFile.parentFile.mkdirs()
        tokenFile.writeText(Json.encodeToString(token))
    }
}