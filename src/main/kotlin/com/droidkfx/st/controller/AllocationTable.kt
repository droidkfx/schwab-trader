package com.droidkfx.st.controller

import com.droidkfx.st.util.databind.ReadOnlyListDataBinding
import com.droidkfx.st.util.databind.ReadWriteListDataBinding
import com.droidkfx.st.util.databind.readOnly
import com.droidkfx.st.view.AllocationTableController
import com.droidkfx.st.view.model.AllocationRowViewModel

class AllocationTable(
    private val rwData: ReadWriteListDataBinding<AllocationRowViewModel>,
) : AllocationTableController {
    override val data: ReadOnlyListDataBinding<AllocationRowViewModel> = rwData.readOnly()

    override suspend fun addNewRow(newRow: AllocationRowViewModel) {
        rwData.add(newRow)
    }
}