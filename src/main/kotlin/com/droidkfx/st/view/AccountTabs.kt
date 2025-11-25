package com.droidkfx.st.view

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

interface AccountTabsController {
    val accountTabs: ReadOnlyListDataBinding<AccountTabViewModel>
    val canRefresh: ReadOnlyValueDataBinding<Boolean>
    val accountTabProvider: (AccountTabViewModel) -> AccountTabController

    suspend fun refreshAllAccounts()
}

class AccountTabs(
    private val c: AccountTabsController,
) : JTabbedPane() {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        buildTabs()
        c.accountTabs.addSwingListener {
            this.removeAll()
            buildTabs()
        }
    }

    private fun buildTabs() {
        c.accountTabs.forEachIndexed { index, it ->
            addTab(it.accountNameDataBinding.value, AccountTab(c.accountTabProvider(it)))
            it.accountNameDataBinding.addSwingListener {
                setTitleAt(index, it)
            }
        }
        if (c.accountTabs.isEmpty()) {
            addTab("Getting Started", JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                add(Box.createVerticalGlue())
                add(JLabel("It does not look like you have synced any accounts yet. Click below to get started").apply {
                    alignmentX = CENTER_ALIGNMENT
                })
                add(Box.createVerticalStrut(10))
                add(JLabel().apply {
                    alignmentX = CENTER_ALIGNMENT
                    if (!c.canRefresh.value) {
                        text = "Please complete Oauth setup to get started Auth -> Update Oauth"
                    }
                    c.canRefresh.addSwingListener {
                        text = if (!it) "Please complete Oauth setup to get started Auth -> Update Oauth" else ""
                        isVisible = !it
                    }
                })
                add(Box.createVerticalStrut(10))
                add(JButton("Refresh Accounts").apply {
                    this.alignmentX = CENTER_ALIGNMENT
                    addCoActionListener { c.refreshAllAccounts() }
                    this.isEnabled = c.canRefresh.value
                    c.canRefresh.addSwingListener { this.isEnabled = it }
                })
                add(Box.createVerticalGlue())
            })
        }
    }
}