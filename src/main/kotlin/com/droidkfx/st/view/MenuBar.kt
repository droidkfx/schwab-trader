package com.droidkfx.st.view

import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import com.droidkfx.st.view.setting.SettingsDialog
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JOptionPane

interface MenuBarController {
    val updateOauthEnabled: ReadOnlyValueDataBinding<Boolean>
    val invalidateOauthEnabled: ReadOnlyValueDataBinding<Boolean>

    suspend fun onOauthUpdate()
    suspend fun onOauthInvalidate()
    suspend fun onClearAllData()
}

class MenuBar(
    c: MenuBarController,
    private val settingsDialog: SettingsDialog
) : JMenuBar() {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        add(JMenu("Menu").apply {
            this.add(JMenuItem("Reset Data").apply {
                addSwingListener {
                    val result = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to clear all data?",
                        "Clear All Data",
                        JOptionPane.YES_NO_OPTION
                    )
                    when (result) {
                        JOptionPane.YES_OPTION -> c.onClearAllData()
                    }
                }
            })
            this.add(JMenuItem("Settings").apply {
                addSwingListener {
                    logger.trace { "onSettings" }
                    settingsDialog.showDialog()
                }
            })
            this.addSeparator()
            this.add(JMenuItem("Update Oath").apply {
                addCoActionListener { c.onOauthUpdate() }
                this.isEnabled = c.updateOauthEnabled.value
                c.updateOauthEnabled.addSwingListener { this.isEnabled = it }
            })
            this.add(JMenuItem("Oauth Invalidate").apply {
                addCoActionListener { c.onOauthInvalidate() }
                this.isEnabled = c.invalidateOauthEnabled.value
                c.invalidateOauthEnabled.addSwingListener { this.isEnabled = it }
            })
        })
    }
}


