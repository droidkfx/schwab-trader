package com.droidkfx.st.view.account

import com.droidkfx.st.util.databind.DataBinding
import com.droidkfx.st.util.databind.ReadOnlyDataBinding
import com.droidkfx.st.view.addCoActionListener
import com.droidkfx.st.view.addCoListChangeListener
import com.droidkfx.st.view.addSwingListener
import io.github.oshai.kotlinlogging.KotlinLogging
import java.awt.BorderLayout
import java.util.Vector
import javax.swing.JButton
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

abstract class ManageAccountList(
    val selectedAccountName: DataBinding<String?>,
    val accountNames: ReadOnlyDataBinding<List<String>>
) : JPanel(BorderLayout()) {
    protected val logger = KotlinLogging.logger {}

    init {
        logger.trace { "Initializing" }

        val jList = JList(Vector(accountNames.value)).apply {
            selectedIndex = 0
            addCoListChangeListener {
                if (it.valueIsAdjusting) return@addCoListChangeListener
                if (selectedIndex < 0 || selectedIndex >= accountNames.value.size) return@addCoListChangeListener
                listSelectionChanged(accountNames.value[selectedIndex])
            }
            accountNames.addSwingListener {
                val indexOfPreviouslySelectedValue = it.indexOf(selectedAccountName.value)
                setListData(Vector(it))
                selectedIndex = if (indexOfPreviouslySelectedValue < 0) 0 else indexOfPreviouslySelectedValue
            }
            selectedAccountName.addSwingListener {
                selectedIndex = it?.let { it1 -> accountNames.value.indexOf(it1) } ?: 0
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

    abstract suspend fun listSelectionChanged(name: String)
    abstract suspend fun refresh()
}