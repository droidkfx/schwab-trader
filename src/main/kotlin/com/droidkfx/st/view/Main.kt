package com.droidkfx.st.view

import java.awt.BorderLayout
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JPanel

open class Main() : JFrame("Schwab Trader") {
    val rootNode = JPanel()

    init {
        // Setup Panel
        rootNode.setLayout(BorderLayout(0, 0))

        // Setup frame
        javaClass.getResource("AppIcon.png")?.let {
            iconImage = ImageIcon(it).image
        }
        this.apply {
            setSize(800, 600)
            defaultCloseOperation = EXIT_ON_CLOSE
            setLocationRelativeTo(null)
            contentPane.add(rootNode, BorderLayout.CENTER)
            isVisible = true
        }
    }
}