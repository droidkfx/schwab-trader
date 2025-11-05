package com.droidkfx.st.view.setting

import com.droidkfx.st.config.ConfigEntity
import com.droidkfx.st.util.databind.ValueDataBinding
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.awt.BorderLayout
import java.awt.Frame
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

@Suppress("USELESS_CAST") // Cast is required for overload ambiguity on Frame
abstract class SettingsDialog(
    private val applicationConfig: ValueDataBinding<ConfigEntity>,
) : JDialog(
    null as? Frame,
    "Application Settings",
    true,
) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        add(JPanel(BorderLayout()).apply {
            border = EmptyBorder(10, 10, 10, 10)
            add(JList(arrayOf("Not yet implemented")), BorderLayout.WEST)
            add(JPanel().apply {
                add(JLabel("Application Settings"))
            }, BorderLayout.CENTER)
        })

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                logger.trace { "Dialog is closing" }
            }
        })
        defaultCloseOperation = HIDE_ON_CLOSE
        pack()
    }

    fun showDialog() {
        setLocationRelativeTo(null)
        isVisible = true
    }
}