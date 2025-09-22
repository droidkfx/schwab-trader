package com.droidkfx.st.oauth

import com.droidkfx.st.schwab.client.OauthClient
import com.droidkfx.st.schwab.client.OauthTokenResponse
import kotlinx.coroutines.runBlocking

class OauthService(
    val repo: OauthRepository,
    val client: OauthClient,
    val server: LocalOAuthRedirectServer
) {

    fun obtainAuth(): OauthTokenResponse {
        val existingToken = repo.loadExistingToken()
        if (existingToken != null) {
            return existingToken
        } else {
            val newToken = initialAuthorization()
            repo.saveToken(newToken)
            return newToken
        }
    }

    fun initialAuthorization(): OauthTokenResponse {
        return runBlocking {
            val resultDeferred = server.awaitReponse()

            val requestState = client.triggerOauthFlow()

            val result = resultDeferred.await()
            if (result.error != null) {
                throw RuntimeException("Error: ${result.error}")
            } else if (result.code != null) {
                if (result.state != requestState) {
                    throw IllegalStateException("State mismatch")
                }
                val token = client.exchangeToken(result)
                return@runBlocking token
            } else {
                throw IllegalStateException("Unexpected result")
            }
        }
    }
}