package com.droidkfx.st.view

import com.droidkfx.st.databind.ReadOnlyDataBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.SwingUtilities.invokeLater

abstract class MenuBar(
    updateOauthEnabled: ReadOnlyDataBinding<Boolean>,
    invalidateOauthEnabled: ReadOnlyDataBinding<Boolean>
) : JMenuBar() {
    init {
        add(JMenu("File"))
        val editMenu = JMenu("Edit")
        editMenu.add(JMenuItem("Update Oath").apply {
            addActionListener {
                CoroutineScope(Dispatchers.Default).launch {
                    onOauthUpdate()
                }
            }
            isEnabled = updateOauthEnabled.value
            updateOauthEnabled.addListener { invokeLater { isEnabled = it } }
        })
        editMenu.add(JMenuItem("Oauth Invalidate").apply {
            addActionListener {
                CoroutineScope(Dispatchers.Default).launch {
                    onOauthInvalidate()
                }
            }
            isEnabled = invalidateOauthEnabled.value
            invalidateOauthEnabled.addListener { invokeLater { isEnabled = it } }
        })
        add(editMenu)
        add(JMenu("Help"))
    }

    abstract suspend fun onOauthUpdate()
    abstract suspend fun onOauthInvalidate()
}