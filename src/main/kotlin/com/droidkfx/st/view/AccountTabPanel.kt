package com.droidkfx.st.view

import com.droidkfx.st.databind.ReadOnlyDataBinding
import java.awt.FlowLayout
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTabbedPane

abstract class AccountTabPanel(accountTabs: ReadOnlyDataBinding<List<AccountTabView>?>) : JTabbedPane() {
    init {
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
    init {
        layout = FlowLayout(FlowLayout.CENTER)
        if (account != null) {
            add(JLabel("Refresh accounts to get started!"))
        }
    }
}