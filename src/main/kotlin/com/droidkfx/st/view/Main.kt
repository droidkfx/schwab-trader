package com.droidkfx.st.view

import java.awt.BorderLayout
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JPanel

abstract class Main(
    statusBar: StatusBar,
    menuBar: MenuBar
) : JFrame("Schwab Trader") {
    val rootNode = JPanel()

    init {
        // Setup Panel
        rootNode.setLayout(BorderLayout(0, 0))

        // Setup frame
        Main::class.java.getResource("AppIcon.png")?.let {
            iconImage = ImageIcon(it).image
        }
        this.apply {
            setSize(800, 600)
            defaultCloseOperation = EXIT_ON_CLOSE
            setLocationRelativeTo(null)

            jMenuBar = menuBar

            contentPane.add(rootNode, BorderLayout.CENTER)
            contentPane.add(statusBar, BorderLayout.SOUTH)
            isVisible = true
        }
    }
}
