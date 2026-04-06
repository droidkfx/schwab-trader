package com.droidkfx.st.view

import com.droidkfx.st.position.AccountPositionService
import com.droidkfx.st.schwab.oauth.OauthService
import com.droidkfx.st.util.databind.toDataBinding
import com.droidkfx.st.util.progress.ProgressService
import com.droidkfx.st.view.model.AccountTabViewModelFactory
import com.droidkfx.st.view.model.AccountTabsViewModel
import com.droidkfx.st.view.model.MenuBarViewModel
import com.droidkfx.st.view.model.StatusBarViewModel
import com.droidkfx.st.view.setting.settingsModule
import com.formdev.flatlaf.FlatDarkLaf
import kotlinx.coroutines.runBlocking
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val accountPositionsBinding = "accountPositionsDataBind"

val viewModule = module {
    FlatDarkLaf.setup()
    includes(settingsModule)

    // ViewModels
    singleOf(::ProgressService)
    single(named(accountPositionsBinding)) {
        runBlocking {
            get<AccountPositionService>().getAccountPositions()
                .toMutableList()
                .toDataBinding()
        }
    }
    singleOf(::StatusBarViewModel)
    singleOf(::AccountTabViewModelFactory)
    single {
        MenuBarViewModel(get(), get(), get(), get(named(accountPositionsBinding)), get())
    }
    single {
        AccountTabsViewModel(
            get(), get(), get(),
            get(named(accountPositionsBinding)),
            get<OauthService>().getTokenStatusBinding(),
            get(),
        )
    }

    // Views
    singleOf(::StatusBar)
    singleOf(::MenuBar)
    singleOf(::AccountTabs)
    singleOf(::Main)
}
