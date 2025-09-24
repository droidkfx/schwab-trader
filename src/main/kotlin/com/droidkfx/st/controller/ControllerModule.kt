package com.droidkfx.st.controller

import com.droidkfx.st.oauth.OauthModule
import com.formdev.flatlaf.FlatDarkLaf

class ControllerModule(oathModule: OauthModule) {
    init {
        FlatDarkLaf.setup()
    }

    val menuBarController = MenuBar(oathModule.oauthService)
    val statusBarController = StatusBar(oathModule.oauthService)
    val accountTabs = AccountTabs()
    val mainController = Main(
        statusBarController = statusBarController,
        menuBarController = menuBarController,
        accountTabs = accountTabs,
    )
}