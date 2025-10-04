package com.droidkfx.st.controller.account

import com.droidkfx.st.databind.DataBinding
import com.droidkfx.st.view.account.ManageAccountList

internal class ManageAccountList(
    val selectedAccountName: DataBinding<String?>,
    accountNames: List<String>,
) :
    ManageAccountList(accountNames) {

    override fun listSelectionChanged(name: String) {
        selectedAccountName.value = name
    }
}