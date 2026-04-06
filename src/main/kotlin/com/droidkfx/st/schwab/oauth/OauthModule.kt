package com.droidkfx.st.schwab.oauth

import com.droidkfx.st.config.CONFIG_ENTITY
import com.droidkfx.st.config.ConfigEntity
import com.droidkfx.st.schwab.OAUTH_REFRESH_SIGNAL
import com.droidkfx.st.schwab.OAUTH_TOKEN
import com.droidkfx.st.schwab.OAUTH_TOKEN_STATUS
import com.droidkfx.st.schwab.oauth.cert.CertificateKeytool
import com.droidkfx.st.schwab.oauth.cert.CertificateService
import com.droidkfx.st.schwab.oauth.cert.OsTrustStore
import com.droidkfx.st.util.databind.ValueDataBinding
import com.droidkfx.st.util.databind.readOnlyMapped
import org.koin.core.qualifier.named
import org.koin.dsl.module

val oauthModule = module {
    single<OsTrustStore> { OsTrustStore.forCurrentOs() }
    single<CertificateKeytool> { CertificateKeytool.jdk() }
    single { CertificateService(get(), get(), get()) }
    single {
        LocalServer(
            get<ValueDataBinding<ConfigEntity>>(named(CONFIG_ENTITY))
                .readOnlyMapped { it.schwabConfig.callbackServerConfig }
        )
    }
    single { OauthRepository(get<ValueDataBinding<ConfigEntity>>(named(CONFIG_ENTITY)).readOnlyMapped { it.repositoryRoot }) }

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