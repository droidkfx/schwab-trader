package com.droidkfx.st.view

import com.droidkfx.st.databind.ReadOnlyDataBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.swing.JMenuItem
import javax.swing.SwingUtilities

internal fun <T> ReadOnlyDataBinding<T>.addSwingListener(listener: (T) -> Unit) {
    addListener { SwingUtilities.invokeLater { listener(it) } }
}

internal fun JMenuItem.addCoActionListener(function: suspend () -> Unit) {
    addActionListener {
        CoroutineScope(Dispatchers.Default).launch {
            function()
        }
    }
}

private const val goldenRatio = 1.618

fun PerfectSize(size: Int) = java.awt.Dimension((size * goldenRatio).toInt(), size)