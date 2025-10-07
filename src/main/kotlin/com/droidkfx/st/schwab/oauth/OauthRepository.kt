package com.droidkfx.st.schwab.oauth

import com.droidkfx.st.config.ConfigEntity
import com.droidkfx.st.schwab.client.OauthTokenResponse
import com.droidkfx.st.util.repository.FileRepository

class OauthRepository(configEntity: ConfigEntity) : FileRepository("${configEntity.repositoryRoot}/oauth") {
    fun loadExistingToken(): OauthTokenResponse? {
        logger.trace { "loadExistingToken" }
        return load("token")
    }

    fun saveToken(token: OauthTokenResponse) {
        logger.trace { "saveToken $token" }
        save("token", token)
    }

    fun deleteToken() {
        logger.trace { "deleteToken" }
        delete("token")
    }
}