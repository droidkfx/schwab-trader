package com.droidkfx.st.view

import com.droidkfx.st.util.databind.ReadOnlyDataBinding
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem

abstract class MenuBar(
    updateOauthEnabled: ReadOnlyDataBinding<Boolean>,
    invalidateOauthEnabled: ReadOnlyDataBinding<Boolean>
) : JMenuBar() {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        add(JMenu("File").apply {
            this.add(JMenuItem("Manage Accounts").apply {
                addCoActionListener { onManageAccounts() }
            })
            this.add(JMenuItem("Clear all data").apply {
                addCoActionListener { onClearAllData() }
            })
        })
        add(JMenu("Auth").apply {
            this.add(JMenuItem("Update Oath").apply {
                addCoActionListener { onOauthUpdate() }
                this.isEnabled = updateOauthEnabled.value
                updateOauthEnabled.addSwingListener { this.isEnabled = it }
            })
            this.add(JMenuItem("Oauth Invalidate").apply {
                addCoActionListener { onOauthInvalidate() }
                this.isEnabled = invalidateOauthEnabled.value
                invalidateOauthEnabled.addSwingListener { this.isEnabled = it }
            })
        })
    }

    abstract suspend fun onOauthUpdate()
    abstract suspend fun onOauthInvalidate()
    abstract suspend fun onManageAccounts()
    abstract suspend fun onClearAllData()
}


