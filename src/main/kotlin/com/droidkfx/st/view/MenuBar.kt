package com.droidkfx.st.view

import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem

interface MenuBarController {
    val updateOauthEnabled: ReadOnlyValueDataBinding<Boolean>
    val invalidateOauthEnabled: ReadOnlyValueDataBinding<Boolean>

    suspend fun onOauthUpdate()
    suspend fun onOauthInvalidate()
    suspend fun onManageAccounts()
    suspend fun onClearAllData()
    suspend fun onSettings()
}

class MenuBar(
    c: MenuBarController
) : JMenuBar() {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        add(JMenu("File").apply {
            this.add(JMenuItem("Manage Accounts").apply {
                addCoActionListener { c.onManageAccounts() }
            })
            this.add(JMenuItem("Reset Data").apply {
                addCoActionListener { c.onClearAllData() }
            })
            this.add(JMenuItem("Settings").apply {
                addSwingListener { c.onSettings() }
            })
        })
        add(JMenu("Auth").apply {
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


