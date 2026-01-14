package com.droidkfx.st.controller

import com.droidkfx.st.position.AccountPositionService
import com.droidkfx.st.schwab.oauth.OauthService
import com.droidkfx.st.util.databind.toDataBinding
import com.droidkfx.st.view.AccountTabsController
import com.droidkfx.st.view.MenuBarController
import com.droidkfx.st.view.StatusBarController
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val accountPositionsBinding = "accountPositionsDataBind"

private val internalControllerModule = module {
    single(named(accountPositionsBinding)) {
        runBlocking {
            get<AccountPositionService>().getAccountPositions()
                .toMutableList()
                .toDataBinding()
        }
    }
}

val controllerModule = module {
    includes(internalControllerModule)
    single {
        MenuBar(
            get(),
            get(),
            get(),
            get(named(accountPositionsBinding))
        ) as MenuBarController
    }
    singleOf(::StatusBar) { bind<StatusBarController>() }
    single {
        AccountTabs(
            get(),
            get(),
            get(named(accountPositionsBinding)),
            GlobalContext.get().get<OauthService>().getTokenStatusBinding()
        ) {
            AccountTab(
                get(),
                get(),
                get(),
                it
            )
        } as AccountTabsController
    }
}
