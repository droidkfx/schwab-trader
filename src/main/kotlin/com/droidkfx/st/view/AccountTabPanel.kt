package com.droidkfx.st.view

import com.droidkfx.st.databind.ReadOnlyDataBinding
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.awt.FlowLayout
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTabbedPane

abstract class AccountTabPanel(accountTabs: ReadOnlyDataBinding<List<AccountTabView>?>) : JTabbedPane() {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        tabPlacement = JTabbedPane.TOP
        accountTabs.value
            ?.forEach { addTab(it.title, it) }
            ?: run {
                addTab("Getting Started", JPanel().apply {
                    layout = GridBagLayout()
                    add(JButton("Click here to fetch accounts"))
                })
            }
    }
}

abstract class AccountTabView(
    val title: String,
    // TODO this should be an account view model
    val account: String? = null
) : JPanel() {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        layout = FlowLayout(FlowLayout.CENTER)
        if (account != null) {
            add(JLabel("Refresh accounts to get started!"))
        }
    }
}