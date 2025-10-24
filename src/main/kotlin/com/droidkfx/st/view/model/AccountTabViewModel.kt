package com.droidkfx.st.view.model

import com.droidkfx.st.account.Account
import com.droidkfx.st.util.databind.ReadWriteListDataBinding
import com.droidkfx.st.util.databind.ValueDataBinding
import java.math.BigDecimal

data class AccountTabViewModel(
    val account: Account,
    val data: ReadWriteListDataBinding<AllocationRowViewModel>,
    val accountCash: ValueDataBinding<BigDecimal> = ValueDataBinding(BigDecimal.ZERO),
    val accountNameDataBinding: ValueDataBinding<String> = ValueDataBinding(account.name)
) {
    fun setAccountName(name: String) {
        account.name = name
        accountNameDataBinding.value = name
    }
}