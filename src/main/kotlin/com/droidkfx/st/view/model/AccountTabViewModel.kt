package com.droidkfx.st.view.model

import com.droidkfx.st.account.Account
import com.droidkfx.st.util.databind.ReadWriteListDataBinding
import com.droidkfx.st.util.databind.ValueDataBinding

data class AccountTabViewModel(
    val account: Account,
    val data: ReadWriteListDataBinding<AllocationRowViewModel>,
    val accountCash: ValueDataBinding<Double> = ValueDataBinding(0.0),
    val accountNameDataBinding: ValueDataBinding<String> = ValueDataBinding(account.name)
) {
    fun setAccountName(name: String) {
        account.name = name
        accountNameDataBinding.value = name
    }
}