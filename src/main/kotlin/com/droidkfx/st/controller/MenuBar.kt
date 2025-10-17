package com.droidkfx.st.controller

import com.droidkfx.st.controller.account.ManageAccountsDialog
import com.droidkfx.st.schwab.oauth.OauthService
import com.droidkfx.st.schwab.oauth.OauthStatus
import com.droidkfx.st.util.databind.mapped
import com.droidkfx.st.view.MenuBar
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class MenuBar(
    private val oauthService: OauthService,
    private val manageAccountsDialog: ManageAccountsDialog
) : MenuBar(
    oauthService.tokenStatus.mapped(::oauthEnabled),
    oauthService.tokenStatus.mapped(::invalidateEnabled)
) {
    private val logger = logger {}

    override suspend fun onOauthUpdate() {
        logger.trace { "onOauthUpdate" }
        oauthService.obtainAuth()
    }

    override suspend fun onOauthInvalidate() {
        logger.trace { "onOauthInvalidate" }
        oauthService.invalidateOauth()
    }

    override suspend fun onManageAccounts() {
        logger.trace { "onManageAccounts" }
        manageAccountsDialog.showDialog()
    }

    override suspend fun onClearAllData() {
        TODO("Not yet implemented")
    }

    companion object {
        fun oauthEnabled(status: OauthStatus): Boolean = when (status) {
            OauthStatus.READY, OauthStatus.INITIALIZING -> false
            OauthStatus.EXPIRED, OauthStatus.NOT_INITIALIZED -> true
        }

        fun invalidateEnabled(status: OauthStatus): Boolean = when (status) {
            OauthStatus.READY, OauthStatus.EXPIRED -> true
            OauthStatus.INITIALIZING, OauthStatus.NOT_INITIALIZED -> false
        }
    }
}