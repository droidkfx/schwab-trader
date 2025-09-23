package com.droidkfx.st.view

import com.droidkfx.st.databind.ReadOnlyDataBinding
import javax.swing.SwingUtilities

internal fun <T> ReadOnlyDataBinding<T>.addSwingListener(listener: (T) -> Unit) {
    addListener { SwingUtilities.invokeLater { listener(it) } }
}