package com.droidkfx.st.view

import com.droidkfx.st.view.model.AccountTabsViewModel
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTabbedPane

class AccountTabs(
    private val vm: AccountTabsViewModel,
) : JTabbedPane() {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        buildTabs()
        vm.accountTabs.addSwingListener {
            this.removeAll()
            buildTabs()
        }
    }

    private fun buildTabs() {
        vm.accountTabs.forEachIndexed { index, tabVm ->
            addTab(tabVm.accountNameDataBinding.value, AccountTab(tabVm))
            tabVm.accountNameDataBinding.addSwingListener {
                setTitleAt(index, it)
            }
        }
        if (vm.accountTabs.isEmpty()) {
            addTab("Getting Started", JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                add(Box.createVerticalGlue())
                add(JLabel("It does not look like you have synced any accounts yet. Click below to get started").apply {
                    alignmentX = CENTER_ALIGNMENT
                })
                add(Box.createVerticalStrut(10))
                add(JLabel().apply {
                    alignmentX = CENTER_ALIGNMENT
                    if (!vm.canRefresh.value) {
                        text = "Please complete Oauth setup to get started Auth -> Update Oauth"
                    }
                    vm.canRefresh.addSwingListener {
                        text = if (!it) "Please complete Oauth setup to get started Auth -> Update Oauth" else ""
                        isVisible = !it
                    }
                })
                add(Box.createVerticalStrut(10))
                add(JButton("Refresh Accounts").apply {
                    this.alignmentX = CENTER_ALIGNMENT
                    addCoActionListener { vm.refreshAllAccounts() }
                    this.isEnabled = vm.canRefresh.value
                    vm.canRefresh.addSwingListener { this.isEnabled = it }
                })
                add(Box.createVerticalGlue())
            })
        }
    }
}
