package com.droidkfx.st.controller

import com.droidkfx.st.databind.mapped
import com.droidkfx.st.oauth.OauthService
import com.droidkfx.st.oauth.OauthStatus
import com.droidkfx.st.view.StatusBar

class StatusBar(oauthService: OauthService) : StatusBar(
    oauthService.getStatus().mapped(::oauthStatusToString)
) {
    companion object {
        fun oauthStatusToString(status: OauthStatus) = when (status) {
            OauthStatus.READY -> "ready"
            OauthStatus.EXPIRED -> "expired"
            OauthStatus.INITIALIZING -> "initializing"
            OauthStatus.NOT_INITIALIZED -> "not initialized"
        }
    }
}