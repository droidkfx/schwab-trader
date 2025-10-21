package com.droidkfx.st.view

import com.droidkfx.st.util.databind.ListDataBindingEvent
import com.droidkfx.st.util.databind.ReadOnlyListDataBinding
import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.swing.JButton
import javax.swing.JList
import javax.swing.JMenuItem
import javax.swing.SwingUtilities
import javax.swing.event.ListSelectionEvent

internal fun <T> ReadOnlyValueDataBinding<T>.addSwingListener(listener: (T) -> Unit) {
    addListener { SwingUtilities.invokeLater { listener(it) } }
}

internal fun <T> ReadOnlyListDataBinding<T>.addSwingListener(listener: (ListDataBindingEvent<T>) -> Unit) {
    addListener { SwingUtilities.invokeLater { listener(it) } }
}

internal fun JMenuItem.addCoActionListener(function: suspend () -> Unit) {
    addActionListener {
        CoroutineScope(Dispatchers.Default).launch {
            function()
        }
    }
}

internal fun JList<*>.addCoListChangeListener(function: suspend (ListSelectionEvent) -> Unit) {
    addListSelectionListener {
        CoroutineScope(Dispatchers.Default).launch {
            function(it)
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

fun GoldenRatioSize(size: Int) = java.awt.Dimension((size * goldenRatio).toInt(), size)