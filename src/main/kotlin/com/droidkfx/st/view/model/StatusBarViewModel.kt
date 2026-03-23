package com.droidkfx.st.view.model

import com.droidkfx.st.schwab.oauth.OauthService
import com.droidkfx.st.schwab.oauth.OauthStatus
import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import com.droidkfx.st.util.databind.readOnlyMapped

class StatusBarViewModel(oauthService: OauthService) {
    val oauthStatus: ReadOnlyValueDataBinding<String> =
        oauthService.getTokenStatusBinding().readOnlyMapped(::oauthStatusToString)

    companion object {
        fun oauthStatusToString(status: OauthStatus): String = when (status) {
            OauthStatus.READY -> "ready"
            OauthStatus.EXPIRED -> "expired"
            OauthStatus.INITIALIZING -> "initializing"
            OauthStatus.NOT_INITIALIZED -> "not initialized"
        }
    }
}
