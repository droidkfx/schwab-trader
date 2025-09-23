package com.droidkfx.st.view

import com.droidkfx.st.databind.ReadOnlyDataBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem

abstract class MenuBar(
    updateOauthEnabled: ReadOnlyDataBinding<Boolean>,
    invalidateOauthEnabled: ReadOnlyDataBinding<Boolean>
) : JMenuBar() {
    init {
        add(JMenu("Auth").apply {
            this.add(JMenuItem("Update Oath").apply {
                addActionListener {
                    CoroutineScope(Dispatchers.Default).launch {
                        onOauthUpdate()
                    }
                }
                this.isEnabled = updateOauthEnabled.value
                updateOauthEnabled.addSwingListener { this.isEnabled = it }
            })
            this.add(JMenuItem("Oauth Invalidate").apply {
                addActionListener {
                    CoroutineScope(Dispatchers.Default).launch {
                        onOauthInvalidate()
                    }
                }
                this.isEnabled = invalidateOauthEnabled.value
                invalidateOauthEnabled.addSwingListener { this.isEnabled = it }
            })
        })
    }

    abstract suspend fun onOauthUpdate()
    abstract suspend fun onOauthInvalidate()
}