package com.droidkfx.st.schwab

import com.droidkfx.st.schwab.client.schwabClientModule
import com.droidkfx.st.schwab.oauth.OauthStatus
import com.droidkfx.st.schwab.oauth.oauthModule
import com.droidkfx.st.util.databind.ValueDataBinding
import com.droidkfx.st.util.databind.toDataBinding
import org.koin.core.qualifier.named
import org.koin.dsl.module

val OAUTH_TOKEN = "oauthToken"
val OAUTH_TOKEN_STATUS = "oauthTokenStatus"
val OAUTH_REFRESH_SIGNAL = "tokenRefreshSignal"

val schwabModule = module {
    includes(schwabClientModule, oauthModule)
    single<ValueDataBinding<String?>>(named(OAUTH_TOKEN)) { ValueDataBinding(null) }
    single<ValueDataBinding<OauthStatus>>(named(OAUTH_TOKEN_STATUS)) { OauthStatus.NOT_INITIALIZED.toDataBinding() }
    single<ValueDataBinding<Boolean>>(named(OAUTH_REFRESH_SIGNAL)) { false.toDataBinding() }
}