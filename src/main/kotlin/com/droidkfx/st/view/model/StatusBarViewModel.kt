package com.droidkfx.st.view.model

import com.droidkfx.st.schwab.oauth.OauthService
import com.droidkfx.st.schwab.oauth.OauthStatus
import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import com.droidkfx.st.util.databind.readOnlyMapped
import com.droidkfx.st.util.progress.ProgressService

class StatusBarViewModel(oauthService: OauthService, progressService: ProgressService) {
    val oauthStatus: ReadOnlyValueDataBinding<String> =
        oauthService.getTokenStatusBinding().readOnlyMapped(::oauthStatusToString)
    val progressText: ReadOnlyValueDataBinding<String> = progressService.displayText

    companion object {
        fun oauthStatusToString(status: OauthStatus): String = when (status) {
            OauthStatus.READY -> "ready"
            OauthStatus.EXPIRED -> "expired"
            OauthStatus.INITIALIZING -> "initializing"
            OauthStatus.NOT_INITIALIZED -> "not initialized"
        }
    }
}
