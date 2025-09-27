package com.droidkfx.st.view

import com.droidkfx.st.controller.AccountTabs
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.awt.BorderLayout
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JPanel

abstract class Main(
    statusBar: StatusBar,
    menuBar: MenuBar,
    accountTabs: AccountTabs
) : JFrame("Schwab Trader") {
    private val logger = logger {}
    val rootNode = JPanel()

    init {
        logger.trace { "Initializing" }
        // Setup Panel
        rootNode.setLayout(BorderLayout(0, 0))

        // Setup frame
        Main::class.java.getResource("AppIcon.png")?.let {
            logger.trace { "Setting icon from: $it" }
            iconImage = ImageIcon(it).image
        } ?: run {
            logger.warn { "No icon found @ 'AppIcon.png'" }
        }
        this.apply {
            setSize(1000, 600)
            defaultCloseOperation = EXIT_ON_CLOSE
            setLocationRelativeTo(null)

            jMenuBar = menuBar

            contentPane.add(accountTabs, BorderLayout.CENTER)
            contentPane.add(statusBar, BorderLayout.SOUTH)
            isVisible = true
        }
    }
}
