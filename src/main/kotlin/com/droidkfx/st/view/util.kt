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
import javax.swing.JList
import javax.swing.JMenuItem
import javax.swing.event.ListSelectionEvent

internal fun <T> ReadOnlyValueDataBinding<T>.addSwingListener(listener: DataBindChangeListener<T>) {
    addListener { oldValue, newValue ->
        CoroutineScope(Dispatchers.Swing).launch {
            listener(oldValue, newValue)
        }
    }
}

internal fun <T> ReadOnlyValueDataBinding<T>.addSwingListener(listener: DataBindValueListener<T>) {
    this.addSwingListener(listener.asChangeListener())
}

internal fun <T> ListDataBinding<T>.addSwingListener(listener: (ListDataBindingEvent<T>) -> Unit) {
    addListener { CoroutineScope(Dispatchers.Swing).launch { listener(it) } }
}

internal fun JMenuItem.addCoActionListener(function: suspend () -> Unit) {
    addActionListener {
        CoroutineScope(Dispatchers.Default).launch {
            function()
        }
    }
}

internal fun JMenuItem.addSwingListener(function: suspend () -> Unit) {
    addActionListener {
        CoroutineScope(Dispatchers.Swing).launch {
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

fun goldenRatioSize(size: Int) = java.awt.Dimension((size * goldenRatio).toInt(), size)