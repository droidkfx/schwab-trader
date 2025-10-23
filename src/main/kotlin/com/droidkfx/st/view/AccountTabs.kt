package com.droidkfx.st.view

import com.droidkfx.st.controller.AccountTab
import com.droidkfx.st.util.databind.ReadOnlyListDataBinding
import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import com.droidkfx.st.view.model.AccountTabViewModel
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTabbedPane

abstract class AccountTabs(
    private val accountTabs: ReadOnlyListDataBinding<AccountTabViewModel>,
    private val canRefresh: ReadOnlyValueDataBinding<Boolean>,
    private val accountTabProvider: (AccountTabViewModel) -> AccountTab
) : JTabbedPane() {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        buildTabs()
        accountTabs.addSwingListener {
            this.removeAll()
            buildTabs()
        }
    }

    private fun buildTabs() {
        accountTabs.forEachIndexed { index, it ->
            addTab(it.account.name, accountTabProvider(it))
            it.accountNameDataBinding.addSwingListener {
                setTitleAt(index, it)
            }
        }
        if (accountTabs.isEmpty()) {
            addTab("Getting Started", JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                add(Box.createVerticalGlue())
                add(JLabel("It does not look like you have synced any accounts yet. Click below to get started").apply {
                    alignmentX = CENTER_ALIGNMENT
                })
                add(Box.createVerticalStrut(10))
                add(JLabel().apply {
                    alignmentX = CENTER_ALIGNMENT
                    if (!canRefresh.value) {
                        text = "Please complete Oauth setup to get started Auth -> Update Oauth"
                    }
                    canRefresh.addSwingListener {
                        text = if (!it) "Please complete Oauth setup to get started Auth -> Update Oauth" else ""
                        isVisible = !it
                    }
                })
                add(Box.createVerticalStrut(10))
                add(JButton("Refresh Accounts").apply {
                    this.alignmentX = CENTER_ALIGNMENT
                    addCoActionListener { refreshAllAccounts() }
                    this.isEnabled = canRefresh.value
                    canRefresh.addSwingListener { this.isEnabled = it }
                })
                add(Box.createVerticalGlue())
            })
        }
    }

    abstract suspend fun refreshAllAccounts()
}