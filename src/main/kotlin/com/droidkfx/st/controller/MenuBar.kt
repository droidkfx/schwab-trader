package com.droidkfx.st.controller

import com.droidkfx.st.databind.mapped
import com.droidkfx.st.oauth.OauthService
import com.droidkfx.st.oauth.OauthStatus
import com.droidkfx.st.view.MenuBar

class MenuBar(private val oauthService: OauthService) : MenuBar(
    oauthService.tokenStatus.mapped(::oauthEnabled),
    oauthService.tokenStatus.mapped(::invalidateEnabled)
) {
    override suspend fun onOauthUpdate() {
        oauthService.obtainAuth(doInit = true)
    }

    override suspend fun onOauthInvalidate() {
        oauthService.invalidateOauth()
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