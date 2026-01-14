package com.droidkfx.st.schwab.client

import com.droidkfx.st.config.CONFIG_ENTITY
import com.droidkfx.st.config.ConfigEntity
import com.droidkfx.st.schwab.OAUTH_REFRESH_SIGNAL
import com.droidkfx.st.schwab.OAUTH_TOKEN
import com.droidkfx.st.schwab.OAUTH_TOKEN_STATUS
import com.droidkfx.st.schwab.oauth.OauthStatus
import com.droidkfx.st.util.databind.ValueDataBinding
import com.droidkfx.st.util.databind.readOnlyMapped
import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val internalClientModule = module {
    single {
        HttpClient(Java) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    coerceInputValues = true
                })
            }
        }
    }
}

val schwabClientModule = module {
    includes(internalClientModule)
    val schwabConfig = "schwabConfig"
    single(named(schwabConfig)) {
        get<ValueDataBinding<ConfigEntity>>(named(CONFIG_ENTITY)).readOnlyMapped { it.schwabConfig }
    }
    single {
        OauthClient(
            get(named(schwabConfig)),
            get()
        )
    }
    single {
        AccountsClient(
            get(named(schwabConfig)),
            get(),
            get(named(OAUTH_TOKEN)),
            get(named(OAUTH_REFRESH_SIGNAL)),
            get<ValueDataBinding<OauthStatus>>(named(OAUTH_TOKEN_STATUS))
        )
    }
    single {
        OrdersClient(
            get(named(schwabConfig)),
            get(),
            get(named(OAUTH_TOKEN)),
            get(named(OAUTH_REFRESH_SIGNAL)),
            get<ValueDataBinding<OauthStatus>>(named(OAUTH_TOKEN_STATUS))
        )
    }
    single {
        TransactionsClient(
            get(named(schwabConfig)),
            get(),
            get(named(OAUTH_TOKEN)),
            get(named(OAUTH_REFRESH_SIGNAL)),
            get<ValueDataBinding<OauthStatus>>(named(OAUTH_TOKEN_STATUS))
        )
    }
    single {
        QuotesClient(
            get(named(schwabConfig)),
            get(),
            get(named(OAUTH_TOKEN)),
            get(named(OAUTH_REFRESH_SIGNAL)),
            get<ValueDataBinding<OauthStatus>>(named(OAUTH_TOKEN_STATUS))
        )
    }
    single {
        UserPreferenceClient()
    }
}