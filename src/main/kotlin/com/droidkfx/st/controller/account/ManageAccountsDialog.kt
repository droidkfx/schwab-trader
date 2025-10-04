package com.droidkfx.st.controller.account

import com.droidkfx.st.position.AccountPositionService
import com.droidkfx.st.view.account.ManageAccountsDialog

class ManageAccountsDialog(accountPositionService: AccountPositionService) :
    ManageAccountsDialog(accountPositionService.getAccountPositions())