package com.droidkfx.st.controller.account

import com.droidkfx.st.view.account.ManageAccountList

internal class ManageAccountList(accountNames: List<String>, val onListChange: (String) -> Unit = {}) :
    ManageAccountList(accountNames) {

    private var selected = accountNames.firstOrNull()

    override fun listSelectionChanged(name: String) {
        if (name == selected) return
        onListChange(name)
        selected = name
    }
}