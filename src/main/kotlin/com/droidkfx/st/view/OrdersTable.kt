package com.droidkfx.st.view

import com.droidkfx.st.util.databind.ReadWriteListDataBinding
import com.droidkfx.st.view.model.ObjectTableModel
import com.droidkfx.st.view.model.OrderRowViewModel
import javax.swing.JScrollPane
import javax.swing.JTable

class OrdersTable(data: ReadWriteListDataBinding<OrderRowViewModel>) : JScrollPane() {

    private val tableModel = OrdersTableModel(data)
    private val table = JTable(tableModel).apply { autoCreateRowSorter = true }

    init {
        verticalScrollBarPolicy = VERTICAL_SCROLLBAR_ALWAYS
        setViewportView(table)
        data.addSwingListener { notifyDataChanged() }
    }

    private fun notifyDataChanged() {
        tableModel.fireTableDataChanged()
        table.packColumns()
    }
}

private class OrdersTableModel(data: List<OrderRowViewModel>) :
    ObjectTableModel<OrderRowViewModel>(data, OrderRowViewModel::class.java) {

    override fun isCellEditable(rowIndex: Int, columnIndex: Int) = false
}
