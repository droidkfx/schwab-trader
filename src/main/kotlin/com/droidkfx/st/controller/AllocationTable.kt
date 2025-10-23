package com.droidkfx.st.controller

import com.droidkfx.st.util.databind.ReadWriteListDataBinding
import com.droidkfx.st.util.databind.readOnly
import com.droidkfx.st.view.AllocationTable
import com.droidkfx.st.view.model.AllocationRowViewModel

class AllocationTable(
    private val data: ReadWriteListDataBinding<AllocationRowViewModel>,
) : AllocationTable(data.readOnly()) {
    override suspend fun addNewRow(newRow: AllocationRowViewModel) {
        data.add(newRow)
    }
}