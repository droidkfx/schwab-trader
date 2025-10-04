package com.droidkfx.st.view

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.awt.Frame
import java.awt.GridBagLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JPanel

@Suppress("USELESS_CAST") // Cast is required for overload ambiguity
abstract class ManageAccounts : JDialog(null as? Frame, "Manage Accounts", true) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        add(JPanel(GridBagLayout()).apply {
            add(JPanel().apply {
                add(JLabel("Manage Accounts menu (WIP)"))
            })
            addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent?) {
                    logger.trace { "Dialog is closing" }
                }
            })
            defaultCloseOperation = HIDE_ON_CLOSE
        })
    }

    fun showDialog() {
        pack()
        setLocationRelativeTo(null)
        isVisible = true
    }
}