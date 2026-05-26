package com.droidkfx.st.view

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.awt.BorderLayout
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JPanel

class Main(statusBar: StatusBar, menuBar: MenuBar, accountTabs: AccountTabs) : JFrame("Schwab Trader") {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }

        // Setup frame
        Main::class.java.getResource("AppIcon.png")?.let {
            logger.trace { "Setting icon from: $it" }
            iconImage = ImageIcon(it).image
        } ?: run {
            logger.warn { "No icon found @ 'AppIcon.png'" }
        }

        // Combined bottom bar: active dock tab strip (left) + status info (right)
        val bottomBar = JPanel(BorderLayout()).apply {
            add(accountTabs.dockStripPanel, BorderLayout.WEST)
            add(statusBar, BorderLayout.CENTER)
        }

        this.apply {
            minimumSize = goldenRatioSize(350)
            defaultCloseOperation = EXIT_ON_CLOSE

            jMenuBar = menuBar

            contentPane.add(accountTabs, BorderLayout.CENTER)
            contentPane.add(bottomBar, BorderLayout.SOUTH)
        }
    }

    fun showAndRun() {
        setLocationRelativeTo(null)
        pack()
        isVisible = true
    }
}
