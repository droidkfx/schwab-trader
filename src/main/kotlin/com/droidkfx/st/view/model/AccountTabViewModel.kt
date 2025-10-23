package com.droidkfx.st.view.model

import com.droidkfx.st.util.databind.ReadWriteListDataBinding

data class AccountTabViewModel(
    val title: String,
    val data: ReadWriteListDataBinding<AllocationRowViewModel>
)