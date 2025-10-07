package com.droidkfx.st.controller

import com.droidkfx.st.util.databind.ReadOnlyDataBinding
import com.droidkfx.st.view.AccountTabPanel
import com.droidkfx.st.view.model.AccountTabViewModel

class AccountTabs(
    accounts: ReadOnlyDataBinding<List<AccountTabViewModel>?>
) : AccountTabPanel(
    accounts
) {
}