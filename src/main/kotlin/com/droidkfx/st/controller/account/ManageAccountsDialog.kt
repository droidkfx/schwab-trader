package com.droidkfx.st.controller.account

import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.util.databind.DataBinding
import com.droidkfx.st.view.account.ManageAccountsDialog

class ManageAccountsDialog internal constructor(
    data: DataBinding<List<AccountPosition>>,
    selectedAccountName: DataBinding<String?>,
    manageAccountList: ManageAccountList
) :
    ManageAccountsDialog(data, selectedAccountName, manageAccountList)