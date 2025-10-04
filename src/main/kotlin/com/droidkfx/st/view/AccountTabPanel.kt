package com.droidkfx.st.view

import com.droidkfx.st.databind.ReadOnlyDataBinding
import com.droidkfx.st.view.model.AccountTabViewModel
import com.droidkfx.st.view.model.AllocationRowViewModel
import com.droidkfx.st.view.model.ObjectTableModel
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTabbedPane
import javax.swing.JTable

abstract class AccountTabPanel(accountTabs: ReadOnlyDataBinding<List<AccountTabViewModel>?>) : JTabbedPane() {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        accountTabs.value?.forEach {
            addTab(
                it.title, JPanel(BorderLayout()).apply {
                    add(
                        JPanel(
                            FlowLayout().apply { alignment = FlowLayout.LEFT }).apply {
                            add(JButton("Adjust allocation"))
                        }, BorderLayout.NORTH
                    )
                    add(AllocationTab(it.data), BorderLayout.CENTER)
                })
        }
        if (accountTabs.value?.isEmpty() == true) {
            addTab("Getting Started", JPanel().apply {
                layout = GridBagLayout()
                add(JLabel("Go to File -> Manage Accounts to start tracking your investments"))
            })
        }
    }

    private class AllocationTab(data: List<AllocationRowViewModel>) : JScrollPane() {
        private val logger = logger {}

        init {
            logger.trace { "Initializing" }
            setViewportView(
                JTable(
                    ObjectTableModel<AllocationRowViewModel>(
                        data = data, AllocationRowViewModel::class.java
                    )
                ).apply {
                    autoCreateRowSorter = true
                })
        }
    }
}