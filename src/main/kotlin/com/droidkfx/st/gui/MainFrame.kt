package com.droidkfx.st.gui

import javax.swing.JFrame

class MainFrame : JFrame() {
    init {
        this.title = "Schwab Trader"
        this.setSize(800, 600)
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.setLocationRelativeTo(null)
        this.isVisible = true
    }
}