package com.droidkfx.st.oauth

import com.droidkfx.st.databind.DataBinding
import com.droidkfx.st.databind.ReadOnlyDataBinding
import com.droidkfx.st.schwab.client.OauthClient
import com.droidkfx.st.schwab.client.OauthTokenResponse
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

class OauthService(
    val repo: OauthRepository,
    val client: OauthClient,
    val server: LocalServer
) {
    val tokenStatus = DataBinding(OauthStatus.NOT_INITIALIZED)
    private var existingToken: OauthTokenResponse? = obtainAuth(doInit = false)

    fun getStatus(): ReadOnlyDataBinding<OauthStatus> = tokenStatus

    fun obtainAuth(doInit: Boolean): OauthTokenResponse? {
        if (existingToken == null) {
            existingToken = repo.loadExistingToken()
        }
        if (existingToken == null && doInit) {
            existingToken = runInitialAuthorization()?.apply {
                repo.saveToken(this)
            }
        }
        return existingToken?.apply {
            tokenStatus.value = OauthStatus.READY
        } ?: run {
            tokenStatus.value = OauthStatus.NOT_INITIALIZED
            null
        }
    }

    fun invalidateOauth() {
        repo.deleteToken()
        existingToken = null
        tokenStatus.value = OauthStatus.NOT_INITIALIZED
    }

    private fun runInitialAuthorization(): OauthTokenResponse? {
        println("Running initial authorization")
        existingToken = runBlocking {
            tokenStatus.value = OauthStatus.INITIALIZING
            val resultDeferred = server.awaitReponse()

            val requestState = client.triggerOauthFlow()

            val result: LocalServer.Result = try {
                withTimeout(120_000) {
                    resultDeferred.await()
                }
            } catch (_: TimeoutCancellationException) {
                server.stop()
                return@runBlocking null
            }

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
        return existingToken
    }
}