package com.droidkfx.st.view.setting

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.awt.Frame
import java.awt.GridBagLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JPanel

@Suppress("USELESS_CAST") // Cast is required for overload ambiguity on Frame
abstract class SettingsDialog() : JDialog(
    null as? Frame,
    "Application Settings",
    true,
) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        add(JPanel(GridBagLayout()).apply {
            add(JLabel("Not yet implemented"))
            add(
                JLabel("You can change the application settings in the 'config.json' file located in the 'config' directory."),
            )
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