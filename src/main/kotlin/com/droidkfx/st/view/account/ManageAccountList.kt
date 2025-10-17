package com.droidkfx.st.view.account

import com.droidkfx.st.util.databind.ReadOnlyDataBinding
import com.droidkfx.st.view.addCoActionListener
import com.droidkfx.st.view.addSwingListener
import io.github.oshai.kotlinlogging.KotlinLogging
import java.awt.BorderLayout
import java.util.Vector
import javax.swing.JButton
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

abstract class ManageAccountList(val accountNames: ReadOnlyDataBinding<List<String>>) : JPanel(BorderLayout()) {
    protected val logger = KotlinLogging.logger {}

    init {
        logger.trace { "Initializing" }

        val jList = JList(Vector(accountNames.value)).apply {
            selectedIndex = 0
            addListSelectionListener {
                listSelectionChanged(accountNames.value[selectedIndex])
            }
            accountNames.addSwingListener {
                setListData(Vector(it))
            }
        }
        add(jList, BorderLayout.CENTER)
        add(JPanel().apply {
            add(JButton("Refresh").apply {
                addCoActionListener { refresh() }
            })
            border = EmptyBorder(5, 5, 5, 5)
        }, BorderLayout.SOUTH)
    }

    abstract fun listSelectionChanged(name: String)
    abstract fun refresh()
}