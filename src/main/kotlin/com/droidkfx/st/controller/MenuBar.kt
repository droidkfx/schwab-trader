package com.droidkfx.st.controller

import com.droidkfx.st.account.AccountService
import com.droidkfx.st.controller.account.ManageAccountsDialog
import com.droidkfx.st.controller.setting.SettingsDialog
import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.position.AccountPositionService
import com.droidkfx.st.schwab.oauth.OauthService
import com.droidkfx.st.schwab.oauth.OauthStatus
import com.droidkfx.st.util.databind.ReadWriteListDataBinding
import com.droidkfx.st.util.databind.readOnlyMapped
import com.droidkfx.st.view.MenuBar
import io.github.oshai.kotlinlogging.KotlinLogging.logger

class MenuBar(
    private val settingsDialog: SettingsDialog,
    private val accountPositionService: AccountPositionService,
    private val accountService: AccountService,
    private val oauthService: OauthService,
    private val manageAccountsDialog: ManageAccountsDialog,
    private val accountData: ReadWriteListDataBinding<AccountPosition>
) : MenuBar(
    oauthService.getTokenStatusBinding().readOnlyMapped(::oauthEnabled),
    oauthService.getTokenStatusBinding().readOnlyMapped(::invalidateEnabled)
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
        logger.trace { "onClearAllData" }
        accountPositionService.clear()
        accountService.clear()
        oauthService.invalidateOauth()
        accountData.clear()
        // TODO reset internal data bindings after clear
    }

    override fun onSettings() {
        logger.trace { "onSettings" }
        settingsDialog.showDialog()
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