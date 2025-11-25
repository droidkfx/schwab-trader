package com.droidkfx.st.view

import com.droidkfx.st.util.databind.ReadOnlyListDataBinding
import com.droidkfx.st.view.model.AllocationRowViewModel
import com.droidkfx.st.view.model.ObjectTableModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import java.math.BigDecimal
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.event.TableModelListener

interface AllocationTableController {
    val data: ReadOnlyListDataBinding<AllocationRowViewModel>
    suspend fun addNewRow(newRow: AllocationRowViewModel)
}

class AllocationTable(c: AllocationTableController) : JScrollPane() {
    private val logger = KotlinLogging.logger {}

    private val allocationTableModel: AllocationTableModel = AllocationTableModel(c.data, c::addNewRow)

    init {
        logger.trace { "Initializing" }
        setViewportView(
            JTable(allocationTableModel).apply {
                autoCreateRowSorter = true
            })
        c.data.addSwingListener {
            notifyDataChanged()
        }
    }

    fun addTableModelListener(l: TableModelListener) {
        allocationTableModel.addTableModelListener(l)
    }

    fun notifyDataChanged() {
        allocationTableModel.fireTableDataChanged()
    }
}

private class AllocationTableModel(
    data: List<AllocationRowViewModel>,
    private val addNewRow: suspend (AllocationRowViewModel) -> Unit
) :
    ObjectTableModel<AllocationRowViewModel>(data, AllocationRowViewModel::class.java) {
    private var newRow: AllocationRowViewModel = defaultValue()

    fun defaultValue(): AllocationRowViewModel = AllocationRowViewModel(
        "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
        BigDecimal.ZERO, "", BigDecimal.ZERO
    )

    fun valueSetup(allocationRowViewModel: AllocationRowViewModel): Boolean {
        return allocationRowViewModel.symbol.isNotEmpty() && allocationRowViewModel.allocationTarget > BigDecimal.ZERO
    }

    override fun getRowCount(): Int {
        return super.rowCount + 1
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return if (rowIndex >= super.rowCount) {
            super.isColumnEditable(columnIndex)
        } else {
            super.isCellEditable(rowIndex, columnIndex)
        }
    }

    override fun setValueAt(value: Any?, rowIndex: Int, columnIndex: Int) {
        if (rowIndex >= super.rowCount) {
            setValueOn(newRow, columnIndex, value)
            super.fireTableCellUpdated(rowIndex, columnIndex)
            if (valueSetup(newRow)) {
                CoroutineScope(Dispatchers.Default).launch {
                    addNewRow(newRow)
                    CoroutineScope(Dispatchers.Swing).launch {
                        super.fireTableRowsInserted(super.rowCount, super.rowCount)
                    }
                    newRow = defaultValue()
                }
            }
        } else {
            super.setValueAt(value, rowIndex, columnIndex)
        }
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): String {

        return if (rowIndex >= super.rowCount) {
            val column = columns[columnIndex]
            val dataValue = column.getter?.invoke(newRow)
            column.mapper.mapOut(dataValue ?: "")
        } else {
            super.getValueAt(rowIndex, columnIndex)
        }
    }
}