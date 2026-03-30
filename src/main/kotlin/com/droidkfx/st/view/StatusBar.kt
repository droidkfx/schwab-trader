package com.droidkfx.st.view

import com.droidkfx.st.view.model.StatusBarViewModel
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JPanel

class StatusBar(vm: StatusBarViewModel) : JPanel() {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        layout = BorderLayout()
        border = BorderFactory.createEmptyBorder(2, 8, 2, 8)

        val rightPanel = JPanel(FlowLayout(FlowLayout.LEFT, 8, 0)).apply {
            add(JLabel(vm.progressText.value).apply {
                vm.progressText.addSwingListener { text = it }
            })
            add(JLabel("Oauth Status: ${vm.oauthStatus.value}").apply {
                vm.oauthStatus.addSwingListener { text = "Oauth Status: $it" }
            })
        }
        add(rightPanel, BorderLayout.EAST)
    }
}
