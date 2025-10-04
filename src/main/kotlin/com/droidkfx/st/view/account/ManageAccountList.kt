package com.droidkfx.st.view.account

import io.github.oshai.kotlinlogging.KotlinLogging
import java.awt.BorderLayout
import java.util.Vector
import javax.swing.JButton
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

abstract class ManageAccountList(val accountNames: List<String>) : JPanel(BorderLayout()) {
    private val logger = KotlinLogging.logger {}

    init {
        logger.trace { "Initializing" }

        add(JList(Vector(accountNames)).apply {
            selectedIndex = 0
            addListSelectionListener {
                listSelectionChanged(accountNames[selectedIndex])
            }
        }, BorderLayout.CENTER)
        add(JPanel().apply {
            add(JButton("Refresh"))
            border = EmptyBorder(5, 5, 5, 5)
        }, BorderLayout.SOUTH)
    }

    abstract fun listSelectionChanged(name: String)
}