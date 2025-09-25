package com.droidkfx.st.oauth

import com.droidkfx.st.databind.DataBinding
import com.droidkfx.st.databind.ReadOnlyDataBinding
import com.droidkfx.st.schwab.client.OauthClient
import com.droidkfx.st.schwab.client.OauthTokenResponse
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.time.Instant

class OauthService(
    val repo: OauthRepository,
    val client: OauthClient,
    val server: LocalServer,
    val authToken: DataBinding<String?> = DataBinding(null)
) {
    private val logger = logger {}
    val tokenStatus = DataBinding(OauthStatus.NOT_INITIALIZED)
    private var existingToken: OauthTokenResponse? = obtainAuth(doInit = false, allowRefresh = false)
        set(value) {
            field = value
            authToken.value = value?.accessToken
        }

    fun getStatus(): ReadOnlyDataBinding<OauthStatus> = tokenStatus

    fun obtainAuth(doInit: Boolean = true, allowRefresh: Boolean = true): OauthTokenResponse? {
        logger.trace { "obtainAuth" }
        // Load existing token from file
        if (existingToken == null) {
            existingToken = repo.loadExistingToken()
        }
        // If existing token is expired, try to refresh it
        if (allowRefresh && existingToken?.expiresAt?.isBefore(Instant.now()) == true) {
            logger.debug { "token expired, refreshing" }
            existingToken = existingToken?.refreshToken
                ?.let { client.refreshOauth(it) }
                ?.apply { repo.saveToken(this) }
        }
        // If no existing token, try to obtain one by Oauth
        if (existingToken == null && doInit) {
            logger.debug { "token found, trying to obtain one" }
            existingToken = runInitialAuthorization()
                ?.apply { repo.saveToken(this) }
        }
        return existingToken?.apply {
            tokenStatus.value = if (expiresAt?.isAfter(Instant.now()) ?: false) {
                logger.info { "token is valid" }
                OauthStatus.READY
            } else {
                logger.info { "token is expired" }
                OauthStatus.EXPIRED
            }
        } ?: run {
            logger.info { "token is not initialized" }
            tokenStatus.value = OauthStatus.NOT_INITIALIZED
            null
        }
    }

    fun invalidateOauth() {
        logger.trace { "invalidateOauth" }
        repo.deleteToken()
        existingToken = null
        tokenStatus.value = OauthStatus.NOT_INITIALIZED
    }

    private fun runInitialAuthorization(): OauthTokenResponse? {
        logger.trace { "runInitialAuthorization" }
        existingToken = runBlocking {
            logger.info { "Beginning Oauth flow" }
            tokenStatus.value = OauthStatus.INITIALIZING
            val resultDeferred = server.awaitReponse()

            val requestState = client.triggerOauthFlow()

            val result: LocalServer.Result = try {
                withTimeout(120_000) {
                    resultDeferred.await()
                }
            } catch (_: TimeoutCancellationException) {
                logger.error { "Timeout waiting for server response" }
                server.stop()
                return@runBlocking null
            }

            if (result.error != null) {
                logger.error { "Error: ${result.error}" }
                throw RuntimeException("Error: ${result.error}")
            } else if (result.code != null) {
                if (result.state != requestState) {
                    logger.error { "State mismatch, expected ${requestState}, got ${result.state} - aborting" }
                    throw IllegalStateException("State mismatch")
                }
                val token = client.exchangeOauthToken(result)
                return@runBlocking token
            } else {
                logger.error { "Unexpected result both code and error are null" }
                throw IllegalStateException("Unexpected result")
            }
        }
        return existingToken
    }
}