package com.droidkfx.st.view

import com.droidkfx.st.util.databind.ReadOnlyValueDataBinding
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.awt.FlowLayout
import javax.swing.JLabel
import javax.swing.JPanel

interface StatusBarController {
    val oauthStatus: ReadOnlyValueDataBinding<String>
}

class StatusBar(c: StatusBarController) : JPanel() {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        layout = FlowLayout(FlowLayout.RIGHT)

        add(JLabel("Oauth Status: ${c.oauthStatus.value}").apply {
            c.oauthStatus.addSwingListener { text = "Oauth Status: $it" }
        })
    }
}