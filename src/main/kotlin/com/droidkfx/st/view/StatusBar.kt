package com.droidkfx.st.view

import com.droidkfx.st.databind.ReadOnlyDataBinding
import java.awt.FlowLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingUtilities

abstract class StatusBar(oauthStatus: ReadOnlyDataBinding<String>) : JPanel() {
    init {
        layout = FlowLayout(FlowLayout.RIGHT)

        add(JLabel("Oauth Status: ${oauthStatus.value}").apply {
            oauthStatus.addListener {
                SwingUtilities.invokeLater { text = "Oauth Status: $it" }
            }
        })
    }
}