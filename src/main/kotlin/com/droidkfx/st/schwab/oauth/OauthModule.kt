package com.droidkfx.st.schwab.oauth

import com.droidkfx.st.config.CONFIG_ENTITY
import com.droidkfx.st.config.ConfigEntity
import com.droidkfx.st.schwab.OAUTH_REFRESH_SIGNAL
import com.droidkfx.st.schwab.OAUTH_TOKEN
import com.droidkfx.st.schwab.OAUTH_TOKEN_STATUS
import com.droidkfx.st.util.databind.ValueDataBinding
import com.droidkfx.st.util.databind.readOnlyMapped
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val oauthInternalModule = module {
    single { LocalServer(get<ValueDataBinding<ConfigEntity>>(named(CONFIG_ENTITY)).value.schwabConfig.callbackServerConfig) }
    single { OauthRepository(get<ValueDataBinding<ConfigEntity>>(named(CONFIG_ENTITY)).readOnlyMapped { it.repositoryRoot }) }
}

val oauthModule = module {
    includes(oauthInternalModule)
    single {
        OauthService(
            get(),
            get(),
            get(),
            get(named(OAUTH_TOKEN_STATUS)),
            get(named(OAUTH_TOKEN)),
            get(named(OAUTH_REFRESH_SIGNAL))
        )
    }
}