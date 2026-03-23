package com.droidkfx.st.view

import com.droidkfx.st.view.model.MenuBarViewModel
import com.droidkfx.st.view.setting.SettingsDialog
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JOptionPane

class MenuBar(
    vm: MenuBarViewModel,
    private val settingsDialog: SettingsDialog,
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
                        JOptionPane.YES_OPTION -> vm.onClearAllData()
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
                addCoActionListener { vm.onOauthUpdate() }
                this.isEnabled = vm.updateOauthEnabled.value
                vm.updateOauthEnabled.addSwingListener { this.isEnabled = it }
            })
            this.add(JMenuItem("Oauth Invalidate").apply {
                addCoActionListener { vm.onOauthInvalidate() }
                this.isEnabled = vm.invalidateOauthEnabled.value
                vm.invalidateOauthEnabled.addSwingListener { this.isEnabled = it }
            })
        })
    }
}
