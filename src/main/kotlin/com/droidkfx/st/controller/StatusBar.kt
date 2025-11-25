package com.droidkfx.st.controller

import com.droidkfx.st.schwab.oauth.OauthService
import com.droidkfx.st.schwab.oauth.OauthStatus
import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import com.droidkfx.st.util.databind.readOnlyMapped
import com.droidkfx.st.view.StatusBarController

class StatusBar(oauthService: OauthService) : StatusBarController {
    override val oauthStatus: ReadOnlyValueDataBinding<String> =
        oauthService.getStatus().readOnlyMapped(::oauthStatusToString)

    companion object {
        fun oauthStatusToString(status: OauthStatus) = when (status) {
            OauthStatus.READY -> "ready"
            OauthStatus.EXPIRED -> "expired"
            OauthStatus.INITIALIZING -> "initializing"
            OauthStatus.NOT_INITIALIZED -> "not initialized"
        }
    }
}