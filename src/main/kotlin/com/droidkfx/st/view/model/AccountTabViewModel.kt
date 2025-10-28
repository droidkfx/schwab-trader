package com.droidkfx.st.view.model

import com.droidkfx.st.account.Account
import com.droidkfx.st.position.AccountPosition
import com.droidkfx.st.util.databind.ReadWriteListDataBinding
import com.droidkfx.st.util.databind.ValueDataBinding
import com.droidkfx.st.util.databind.toDataBinding
import java.math.BigDecimal

data class AccountTabViewModel(
    private var accountPosition: AccountPosition,
) {
    val data: ReadWriteListDataBinding<AllocationRowViewModel> = accountPosition.toAllocationRows().toDataBinding()
    val accountNameDataBinding: ValueDataBinding<String> = ValueDataBinding(account.name)
    val accountCash: ValueDataBinding<BigDecimal> = ValueDataBinding(accountPosition.currentCash)

    fun setAccountName(name: String) {
        account.name = name
        accountNameDataBinding.value = name
    }

    val account: Account
        get() = accountPosition.account

    val accountId: String
        get() = account.id

    fun currentAccountPosition(): AccountPosition = accountPosition.copy()

    fun updateAccountPosition(ap: AccountPosition) {
        this.accountPosition = ap
        setAccountName(ap.account.name)
        accountCash.value = ap.currentCash
    }

    fun rebuildAllocationRows() {
        val newRows = accountPosition.toAllocationRows()
        for (row: AllocationRowViewModel in newRows) {
            data.indexOfFirst { it.symbol == row.symbol }.let {
                if (it == -1) {
                    data.add(row)
                } else {
                    data[it] = row
                }
            }
        }
    }
}