package com.droidkfx.st.view

import com.droidkfx.st.util.databind.DataBindChangeListener
import com.droidkfx.st.util.databind.DataBindValueListener
import com.droidkfx.st.util.databind.ListDataBinding
import com.droidkfx.st.util.databind.ListDataBindingEvent
import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import com.droidkfx.st.util.databind.asChangeListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import javax.swing.JButton
import javax.swing.JMenuItem
import javax.swing.JTable

internal fun <T> ReadOnlyValueDataBinding<T>.addSwingListener(
    scope: CoroutineScope = CoroutineScope(Dispatchers.Swing),
    listener: DataBindChangeListener<T>
) {
    addListener { oldValue, newValue ->
        scope.launch {
            listener(oldValue, newValue)
        }
    }
}

internal fun <T> ReadOnlyValueDataBinding<T>.addSwingListener(
    scope: CoroutineScope = CoroutineScope(Dispatchers.Swing),
    listener: DataBindValueListener<T>
) {
    this.addSwingListener(scope, listener.asChangeListener())
}

internal fun <T> ListDataBinding<T>.addSwingListener(
    scope: CoroutineScope = CoroutineScope(Dispatchers.Swing),
    listener: (ListDataBindingEvent<T>) -> Unit
) {
    addListener { scope.launch { listener(it) } }
}

private const val COLUMN_MARGIN = 10

internal fun JTable.packColumns() {
    for (col in 0 until columnCount) {
        val tableColumn = columnModel.getColumn(col)
        val headerComp = tableHeader?.defaultRenderer?.getTableCellRendererComponent(
            this, tableColumn.headerValue, false, false, -1, col
        )
        var colWidth = headerComp?.preferredSize?.width ?: 0
        for (row in 0 until rowCount) {
            val cellComp = getCellRenderer(row, col).getTableCellRendererComponent(
                this, getValueAt(row, col), false, false, row, col
            )
            colWidth = maxOf(colWidth, cellComp.preferredSize.width)
        }
        colWidth += COLUMN_MARGIN
        tableColumn.minWidth = colWidth
        tableColumn.preferredWidth = colWidth
    }
}

internal fun JMenuItem.addCoActionListener(
    scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    function: suspend () -> Unit
) {
    addActionListener {
        scope.launch {
            function()
        }
    }
}

internal fun JMenuItem.addSwingListener(
    scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    function: suspend () -> Unit
) {
    addActionListener {
        scope.launch {
            function()
        }
    }
}

internal fun JButton.addCoActionListener(function: suspend () -> Unit) {
    addActionListener {
        CoroutineScope(Dispatchers.Default).launch {
            function()
        }
    }
}

private const val goldenRatio = 1.618

fun goldenRatioSize(size: Int) = java.awt.Dimension((size * goldenRatio).toInt(), size)