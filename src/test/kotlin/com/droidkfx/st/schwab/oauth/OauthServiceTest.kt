package com.droidkfx.st.schwab.oauth

import com.droidkfx.st.schwab.client.OauthClient
import com.droidkfx.st.schwab.client.OauthTokenResponse
import com.droidkfx.st.util.databind.ValueDataBinding
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class OauthServiceTest {

    private lateinit var repo: OauthRepository
    private lateinit var client: OauthClient
    private lateinit var server: LocalServer
    private lateinit var tokenStatus: ValueDataBinding<OauthStatus>
    private lateinit var tokenRefreshSignal: ValueDataBinding<Boolean>

    @BeforeEach
    fun setUp() {
        repo = mockk(relaxed = true)
        client = mockk(relaxed = true)
        server = mockk(relaxed = true)
        tokenStatus = ValueDataBinding(OauthStatus.NOT_INITIALIZED)
        tokenRefreshSignal = ValueDataBinding(false)
    }

    /**
     * Base64 of {"exp":9999999999} ≈ year 2286, ensuring expiresAt is in the future.
     * The default fixture uses {"exp":200000000} ≈ year 1976 which is expired.
     */
    private fun futureToken(
        refreshToken: String = "refresh-token",
        accessToken: String = "access-token",
    ): OauthTokenResponse = defaultOauthTokenResponse(
        refreshToken = refreshToken,
        accessToken = accessToken,
        idToken = "header.eyJleHAiOjk5OTk5OTk5OTl9.signature",
    )

    // --- token not initialized triggers OAuth flow ---

    private fun setupNoTokenWithOAuthSuccess(
        newToken: OauthTokenResponse = futureToken(),
        state: String = "test-state",
    ): CompletableDeferred<LocalServer.Result> {
        val serverResult = CompletableDeferred<LocalServer.Result>().also {
            it.complete(LocalServer.Result(code = "auth-code", session = null, state = state, error = null))
        }
        every { repo.loadExistingToken() } returns null
        every { client.triggerOauthFlow() } returns state
        every { server.awaitResponse() } returns serverResult
        every { client.exchangeOauthToken(any<LocalServer.Result>()) } returns newToken
        return serverResult
    }

    @Test
    fun `when token is not initialized and refresh signal fires OAuth flow is initiated`() {
        setupNoTokenWithOAuthSuccess()

        OauthService(repo, client, server, tokenStatus, tokenRefreshSignal = tokenRefreshSignal)
        tokenRefreshSignal.value = !tokenRefreshSignal.value

        verify { client.triggerOauthFlow() }
    }

    @Test
    fun `when token is not initialized and OAuth flow succeeds status is READY`() {
        val newToken = futureToken(accessToken = "fresh-access")
        setupNoTokenWithOAuthSuccess(newToken)

        OauthService(repo, client, server, tokenStatus, tokenRefreshSignal = tokenRefreshSignal)
        tokenRefreshSignal.value = !tokenRefreshSignal.value

        assertEquals(OauthStatus.READY, tokenStatus.value)
    }

    @Test
    fun `when token is not initialized and OAuth flow succeeds new token is saved to repo`() {
        val newToken = futureToken(accessToken = "fresh-access")
        setupNoTokenWithOAuthSuccess(newToken)

        OauthService(repo, client, server, tokenStatus, tokenRefreshSignal = tokenRefreshSignal)
        tokenRefreshSignal.value = !tokenRefreshSignal.value

        verify { repo.saveToken(newToken) }
    }

    // --- refreshToken success ---

    @Test
    fun `refreshToken success updates status to READY`() {
        val initialToken = futureToken(refreshToken = "old-refresh")
        val refreshedToken = futureToken(accessToken = "new-access")

        every { repo.loadExistingToken() } returns initialToken
        every { client.refreshOauth("old-refresh") } returns refreshedToken

        OauthService(repo, client, server, tokenStatus, tokenRefreshSignal = tokenRefreshSignal)
        tokenRefreshSignal.value = !tokenRefreshSignal.value

        assertEquals(OauthStatus.READY, tokenStatus.value)
    }

    @Test
    fun `refreshToken success saves refreshed token to repo`() {
        val initialToken = futureToken(refreshToken = "old-refresh")
        val refreshedToken = futureToken(accessToken = "new-access")

        every { repo.loadExistingToken() } returns initialToken
        every { client.refreshOauth("old-refresh") } returns refreshedToken

        OauthService(repo, client, server, tokenStatus, tokenRefreshSignal = tokenRefreshSignal)
        tokenRefreshSignal.value = !tokenRefreshSignal.value

        verify { repo.saveToken(refreshedToken) }
    }

    // --- refreshToken failure triggers OAuth flow ---

    private fun setupFailingRefreshWithOAuthSuccess(
        initialToken: OauthTokenResponse,
        newToken: OauthTokenResponse = futureToken(),
        state: String = "test-state",
    ): CompletableDeferred<LocalServer.Result> {
        val serverResult = CompletableDeferred<LocalServer.Result>().also {
            it.complete(LocalServer.Result(code = "auth-code", session = null, state = state, error = null))
        }
        var loadCount = 0
        every { repo.loadExistingToken() } answers { if (loadCount++ == 0) initialToken else null }
        every { client.refreshOauth(any()) } throws IllegalStateException("Refresh token expired")
        every { client.triggerOauthFlow() } returns state
        every { server.awaitResponse() } returns serverResult
        every { client.exchangeOauthToken(any<LocalServer.Result>()) } returns newToken
        return serverResult
    }

    @Test
    fun `when refreshToken fails OAuth flow is automatically initiated`() {
        val initialToken = futureToken(refreshToken = "expired-refresh")
        setupFailingRefreshWithOAuthSuccess(initialToken)

        OauthService(repo, client, server, tokenStatus, tokenRefreshSignal = tokenRefreshSignal)
        tokenRefreshSignal.value = !tokenRefreshSignal.value

        verify { client.triggerOauthFlow() }
    }

    @Test
    fun `when refreshToken fails stale token is deleted from repo`() {
        val initialToken = futureToken(refreshToken = "expired-refresh")
        setupFailingRefreshWithOAuthSuccess(initialToken)

        OauthService(repo, client, server, tokenStatus, tokenRefreshSignal = tokenRefreshSignal)
        tokenRefreshSignal.value = !tokenRefreshSignal.value

        verify { repo.deleteToken() }
    }

    @Test
    fun `when refreshToken fails and OAuth flow succeeds status is READY`() {
        val initialToken = futureToken(refreshToken = "expired-refresh")
        val newToken = futureToken(accessToken = "brand-new-access")
        setupFailingRefreshWithOAuthSuccess(initialToken, newToken)

        OauthService(repo, client, server, tokenStatus, tokenRefreshSignal = tokenRefreshSignal)
        tokenRefreshSignal.value = !tokenRefreshSignal.value

        assertEquals(OauthStatus.READY, tokenStatus.value)
    }

    @Test
    fun `when refreshToken fails and OAuth flow succeeds new token is saved to repo`() {
        val initialToken = futureToken(refreshToken = "expired-refresh")
        val newToken = futureToken(accessToken = "brand-new-access")
        setupFailingRefreshWithOAuthSuccess(initialToken, newToken)

        OauthService(repo, client, server, tokenStatus, tokenRefreshSignal = tokenRefreshSignal)
        tokenRefreshSignal.value = !tokenRefreshSignal.value

        verify { repo.saveToken(newToken) }
    }
}
