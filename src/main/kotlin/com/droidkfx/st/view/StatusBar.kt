package com.droidkfx.st.view

import com.droidkfx.st.view.model.StatusBarViewModel
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.awt.FlowLayout
import javax.swing.JLabel
import javax.swing.JPanel

class StatusBar(vm: StatusBarViewModel) : JPanel() {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        layout = FlowLayout(FlowLayout.RIGHT)
        add(JLabel("Oauth Status: ${vm.oauthStatus.value}").apply {
            vm.oauthStatus.addSwingListener { text = "Oauth Status: $it" }
        })
    }
}
