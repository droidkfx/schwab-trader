package com.droidkfx.st.view

import com.droidkfx.st.view.model.AccountTabsViewModel
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.awt.BorderLayout
import java.awt.CardLayout
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTabbedPane

class AccountTabs(private val vm: AccountTabsViewModel) : JPanel(BorderLayout()) {
    private val logger = logger {}

    private val tabbedPane = JTabbedPane()

    /** CardLayout panel that shows the active account tab's dock strip.
     *  Placed by Main alongside the StatusBar so they share the same row. */
    private val dockCardLayout = CardLayout()
    val dockStripPanel: JPanel = JPanel(dockCardLayout)

    init {
        logger.trace { "Initializing" }
        add(tabbedPane, BorderLayout.CENTER)
        buildTabs()
        vm.accountTabBundles.addSwingListener {
            tabbedPane.removeAll()
            dockStripPanel.removeAll()
            buildTabs()
        }
        tabbedPane.addChangeListener { showActiveDockStrip() }
    }

    private fun buildTabs() {
        vm.accountTabBundles.forEachIndexed { index, bundle ->
            val accountTab = AccountTab(bundle.accountVm, bundle.ordersVm)
            tabbedPane.addTab(bundle.accountVm.accountNameDataBinding.value, accountTab)
            dockStripPanel.add(accountTab.dock.tabStrip, "dock_$index")
            bundle.accountVm.accountNameDataBinding.addSwingListener {
                tabbedPane.setTitleAt(index, it)
            }
        }
        if (vm.accountTabBundles.isEmpty()) {
            dockStripPanel.add(JPanel(), "dock_empty")
            tabbedPane.addTab(
                "Getting Started",
                JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.Y_AXIS)
                    add(Box.createVerticalGlue())
                    add(
                        JLabel(
                            "It does not look like you have synced any accounts yet. Click below to get started",
                        ).apply {
                            alignmentX = CENTER_ALIGNMENT
                        },
                    )
                    add(Box.createVerticalStrut(10))
                    add(
                        JLabel().apply {
                            alignmentX = CENTER_ALIGNMENT
                            if (!vm.canRefresh.value) {
                                text = "Please complete Oauth setup to get started Auth -> Update Oauth"
                            }
                            vm.canRefresh.addSwingListener {
                                text =
                                    if (!it) "Please complete Oauth setup to get started Auth -> Update Oauth" else ""
                                isVisible = !it
                            }
                        },
                    )
                    add(Box.createVerticalStrut(10))
                    add(
                        JButton("Refresh Accounts").apply {
                            this.alignmentX = CENTER_ALIGNMENT
                            addCoActionListener { vm.refreshAllAccounts() }
                            this.isEnabled = vm.canRefresh.value
                            vm.canRefresh.addSwingListener { this.isEnabled = it }
                        },
                    )
                    add(Box.createVerticalGlue())
                },
            )
        }
        showActiveDockStrip()
    }

    private fun showActiveDockStrip() {
        val idx = tabbedPane.selectedIndex.coerceAtLeast(0)
        val key = if (vm.accountTabBundles.isEmpty()) "dock_empty" else "dock_$idx"
        dockCardLayout.show(dockStripPanel, key)
    }
}
