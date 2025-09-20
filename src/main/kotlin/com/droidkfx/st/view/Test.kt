package com.droidkfx.st.view

import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JPanel

class Test {
    val root = JPanel()
    val testButton = JButton()

    init {
        root.setLayout(GridBagLayout())

        testButton.setText("Run Test")
        val gbc = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            fill = GridBagConstraints.HORIZONTAL
        }
        root.add(testButton, gbc)
    }
}