package com.droidkfx.st.view.model

import com.droidkfx.st.account.AccountService
import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.position.AccountPositionService
import com.droidkfx.st.schwab.oauth.OauthService
import com.droidkfx.st.schwab.oauth.OauthStatus
import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import com.droidkfx.st.util.databind.ReadWriteListDataBinding
import com.droidkfx.st.util.databind.readOnlyMapped
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class MenuBarViewModel(
    private val accountPositionService: AccountPositionService,
    private val accountService: AccountService,
    private val oauthService: OauthService,
    private val accountData: ReadWriteListDataBinding<AccountPosition>,
) {
    private val logger = logger {}

    val updateOauthEnabled: ReadOnlyValueDataBinding<Boolean> =
        oauthService.getTokenStatusBinding().readOnlyMapped(::oauthEnabled)

    val invalidateOauthEnabled: ReadOnlyValueDataBinding<Boolean> =
        oauthService.getTokenStatusBinding().readOnlyMapped(::invalidateEnabled)

    fun onOauthUpdate() {
        logger.trace { "onOauthUpdate" }
        oauthService.obtainAuth()
    }

    fun onOauthInvalidate() {
        logger.trace { "onOauthInvalidate" }
        oauthService.invalidateOauth()
    }

    fun onClearAllData() {
        logger.trace { "onClearAllData" }
        accountPositionService.clear()
        accountService.clear()
        oauthService.invalidateOauth()
        accountData.clear()
        // TODO reset internal data bindings after clear
    }

    companion object {
        private fun oauthEnabled(status: OauthStatus): Boolean = when (status) {
            OauthStatus.READY, OauthStatus.INITIALIZING -> false
            OauthStatus.EXPIRED, OauthStatus.NOT_INITIALIZED -> true
        }

        private fun invalidateEnabled(status: OauthStatus): Boolean = when (status) {
            OauthStatus.READY, OauthStatus.EXPIRED -> true
            OauthStatus.INITIALIZING, OauthStatus.NOT_INITIALIZED -> false
        }
    }
}
