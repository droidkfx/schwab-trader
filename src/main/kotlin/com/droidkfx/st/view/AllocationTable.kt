package com.droidkfx.st.view

import com.droidkfx.st.view.model.AllocationRowViewModel
import com.droidkfx.st.view.model.ObjectTableModel
import io.github.oshai.kotlinlogging.KotlinLogging
import javax.swing.JScrollPane
import javax.swing.JTable

abstract class AllocationTable(data: List<AllocationRowViewModel>) : JScrollPane() {
    private val logger = KotlinLogging.logger {}

    init {
        logger.trace { "Initializing" }
        setViewportView(
            JTable(AllocationTableModel(data)).apply {
                autoCreateRowSorter = true
            })
    }
}

private class AllocationTableModel(data: List<AllocationRowViewModel>) :
    ObjectTableModel<AllocationRowViewModel>(data, AllocationRowViewModel::class.java) {
    private val newRow = MutableList(columnCount) { "" }

    override fun getRowCount(): Int {
        return super.rowCount + 1
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return (rowIndex >= super.rowCount) && super.isColumnEditable(columnIndex)
    }

    override fun setValueAt(value: Any?, rowIndex: Int, columnIndex: Int) {
        if (rowIndex >= super.rowCount) {
            newRow[columnIndex] = value?.toString() ?: ""
        } else {
            super.setValueAt(value, rowIndex, columnIndex)
        }
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): String {
        return if (rowIndex >= super.rowCount) {
            columns[columnIndex].mapper.map(newRow[columnIndex])
        } else {
            super.getValueAt(rowIndex, columnIndex)
        }
    }
}