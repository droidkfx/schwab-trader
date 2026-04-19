package com.droidkfx.st.view

import com.droidkfx.st.util.databind.ReadWriteListDataBinding
import com.droidkfx.st.view.model.AllocationRowViewModel
import com.droidkfx.st.view.model.ObjectTableModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import java.awt.Dimension
import java.awt.event.HierarchyEvent
import java.math.BigDecimal
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.SwingUtilities
import javax.swing.event.TableModelListener

class AllocationTable(data: ReadWriteListDataBinding<AllocationRowViewModel>) : JScrollPane() {
    private val logger = KotlinLogging.logger {}

    private val allocationTableModel: AllocationTableModel = AllocationTableModel(data) { data.add(it) }
    private val table: JTable = JTable(allocationTableModel).apply { autoCreateRowSorter = true }

    init {
        logger.trace { "Initializing" }
        setViewportView(table)
        data.addSwingListener {
            notifyDataChanged()
        }
        addHierarchyListener { e ->
            if (e.changeFlags and HierarchyEvent.SHOWING_CHANGED.toLong() != 0L && isShowing) {
                refreshColumnWidths()
            }
        }
    }

    fun addTableModelListener(l: TableModelListener) {
        allocationTableModel.addTableModelListener(l)
    }

    fun notifyDataChanged() {
        allocationTableModel.fireTableDataChanged()
        refreshColumnWidths()
    }

    private fun refreshColumnWidths() {
        table.packColumns()
        updateFrameMinimumWidth()
    }

    private fun updateFrameMinimumWidth() {
        val frame = SwingUtilities.getWindowAncestor(this) as? JFrame ?: return
        val scrollBarWidth = verticalScrollBar?.preferredSize?.width ?: 0
        val borderInsets = border?.getBorderInsets(this)
        val borderWidth = (borderInsets?.left ?: 0) + (borderInsets?.right ?: 0)
        val minWidth = table.columnModel.totalColumnWidth + scrollBarWidth + borderWidth
        frame.minimumSize = Dimension(minWidth, frame.minimumSize.height)
    }
}

private class AllocationTableModel(
    data: List<AllocationRowViewModel>,
    private val addNewRow: suspend (AllocationRowViewModel) -> Unit,
) : ObjectTableModel<AllocationRowViewModel>(data, AllocationRowViewModel::class.java) {
    private var newRow: AllocationRowViewModel = defaultValue()

    fun defaultValue(): AllocationRowViewModel = AllocationRowViewModel(
        "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
        BigDecimal.ZERO, "", BigDecimal.ZERO
    )

    fun valueSetup(allocationRowViewModel: AllocationRowViewModel): Boolean {
        return allocationRowViewModel.symbol.isNotEmpty() && allocationRowViewModel.allocationTarget > BigDecimal.ZERO
    }

    override fun getRowCount(): Int {
        return super.rowCount + 2
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return if (rowIndex == super.rowCount) {
            super.isColumnEditable(columnIndex)
        } else if (rowIndex > super.rowCount) {
            false
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
        return if (rowIndex == super.rowCount) {
            val column = columns[columnIndex]
            val dataValue = column.getter?.invoke(newRow)
            column.mapper.mapOut(dataValue ?: "")
        } else if (rowIndex > super.rowCount) {
            if (columnIndex == 0) "TOTAL"
            else {
                when (columnIndex) {
                    1, 4, 5, 6, 9 -> {
                        var acc = BigDecimal.ZERO
                        for (i in 0 until super.rowCount) {
                            val rawValueAt = super.getRawValueAt<BigDecimal>(i, columnIndex)
                            acc = acc.plus(rawValueAt ?: BigDecimal.ZERO)
                        }
                        val column = columns[columnIndex]
                        column.mapper.mapOut(acc)
                    }

                    else -> {
                        ""
                    }
                }
            }
        } else {
            super.getValueAt(rowIndex, columnIndex)
        }
    }
}
