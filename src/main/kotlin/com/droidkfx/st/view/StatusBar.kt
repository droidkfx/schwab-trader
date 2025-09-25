package com.droidkfx.st.view

import com.droidkfx.st.databind.ReadOnlyDataBinding
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.awt.FlowLayout
import javax.swing.JLabel
import javax.swing.JPanel

abstract class StatusBar(oauthStatus: ReadOnlyDataBinding<String>) : JPanel() {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        layout = FlowLayout(FlowLayout.RIGHT)

        add(JLabel("Oauth Status: ${oauthStatus.value}").apply {
            oauthStatus.addSwingListener { text = "Oauth Status: $it" }
        })
    }
}