package com.droidkfx.st.view.setting

import com.droidkfx.st.config.ConfigEntity
import com.droidkfx.st.util.databind.ValueDataBinding
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.awt.Frame
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.border.EmptyBorder

@Suppress("USELESS_CAST") // Cast is required for overload ambiguity on Frame
class SettingsDialog(
    private val applicationConfig: ValueDataBinding<ConfigEntity>,
) : JDialog(
    null as? Frame,
    "Application Settings",
    true,
) {
    private val logger = logger {}
    private var newConfig: ConfigEntity = applicationConfig.value
    private val updateSettingsFunctions = mutableListOf<() -> ConfigEntity>()

    init {
        logger.trace { "Initializing" }
        var rowsAdded = 0
        add(JPanel(GridBagLayout()).apply {
            border = EmptyBorder(10, 10, 10, 10)
            add(
                JLabel("Schwab Client Settings").apply {
                    border = EmptyBorder(2, 0, 2, 0)
                },
                GridBagConstraints().apply {
                    gridx = 0
                    gridy = 0
                    gridwidth = 2
                    anchor = GridBagConstraints.WEST
                })
            rowsAdded++
            fun addSettingsRow(label: String, value: String, setter: (String) -> ConfigEntity) {
                add(
                    JLabel(label).apply {
                        border = EmptyBorder(2, 0, 2, 10)
                    },
                    GridBagConstraints().apply {
                        gridx = 0
                        gridy = rowsAdded
                        gridwidth = 1
                        anchor = GridBagConstraints.WEST
                    })
                add(
                    JTextField(value).apply {
                        columns = 50
                        border = EmptyBorder(2, 0, 2, 0)
                        updateSettingsFunctions.add { setter(text) }
                    },
                    GridBagConstraints().apply {
                        gridx = 1
                        gridy = rowsAdded
                        gridwidth = 1
                        anchor = GridBagConstraints.WEST
                    }
                )
                rowsAdded++
            }

            addSettingsRow(
                "Key:",
                applicationConfig.value.schwabConfig.key
            ) { v -> newConfig.copy(schwabConfig = newConfig.schwabConfig.copy(key = v)) }
            addSettingsRow(
                "Secret:",
                applicationConfig.value.schwabConfig.secret
            ) { v -> newConfig.copy(schwabConfig = newConfig.schwabConfig.copy(secret = v)) }
            addSettingsRow(
                "SSL Cert File:",
                applicationConfig.value.schwabConfig.callbackServerConfig.sslCertLocation
            ) { v ->
                newConfig.copy(
                    schwabConfig = newConfig.schwabConfig.copy(
                        callbackServerConfig = newConfig.schwabConfig.callbackServerConfig.copy(
                            sslCertLocation = v
                        )
                    )
                )
            }
            addSettingsRow(
                "SSL Cert Password:",
                applicationConfig.value.schwabConfig.callbackServerConfig.sslCertPassword
            ) { v ->
                newConfig.copy(
                    schwabConfig = newConfig.schwabConfig.copy(
                        callbackServerConfig = newConfig.schwabConfig.callbackServerConfig.copy(
                            sslCertPassword = v
                        )
                    )
                )
            }
            addSettingsRow(
                "SSL Cert Alias:",
                applicationConfig.value.schwabConfig.callbackServerConfig.sslCertAlias
            ) { v ->
                newConfig.copy(
                    schwabConfig = newConfig.schwabConfig.copy(
                        callbackServerConfig = newConfig.schwabConfig.callbackServerConfig.copy(
                            sslCertAlias = v
                        )
                    )
                )
            }
            addSettingsRow(
                "SSL Cert Type:",
                applicationConfig.value.schwabConfig.callbackServerConfig.sslCertType
            ) { v ->
                newConfig.copy(
                    schwabConfig = newConfig.schwabConfig.copy(
                        callbackServerConfig = newConfig.schwabConfig.callbackServerConfig.copy(
                            sslCertType = v
                        )
                    )
                )
            }
            addSettingsRow(
                "Callback server host:",
                applicationConfig.value.schwabConfig.callbackServerConfig.host
            ) { v ->
                newConfig.copy(
                    schwabConfig = newConfig.schwabConfig.copy(
                        callbackServerConfig = newConfig.schwabConfig.callbackServerConfig.copy(
                            host = v
                        )
                    )
                )
            }
            addSettingsRow(
                "Callback server port:",
                applicationConfig.value.schwabConfig.callbackServerConfig.port.toString()
            ) { v ->
                newConfig.copy(
                    schwabConfig = newConfig.schwabConfig.copy(
                        callbackServerConfig = newConfig.schwabConfig.callbackServerConfig.copy(
                            port = Integer.parseInt(v)
                        )
                    )
                )
            }
            addSettingsRow(
                "Callback server path:",
                applicationConfig.value.schwabConfig.callbackServerConfig.callbackPath
            ) { v ->
                newConfig.copy(
                    schwabConfig = newConfig.schwabConfig.copy(
                        callbackServerConfig = newConfig.schwabConfig.callbackServerConfig.copy(
                            callbackPath = v
                        )
                    )
                )
            }
            addSettingsRow(
                "Repository Root:",
                applicationConfig.value.repositoryRoot
            ) { v -> newConfig.copy(repositoryRoot = v) }
        })

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                logger.trace { "Dialog is closing" }
                updateSettingsFunctions.forEach { newConfig = it() }
                applicationConfig.value = newConfig
            }
        })
        defaultCloseOperation = HIDE_ON_CLOSE
        pack()
    }

    fun showDialog() {
        setLocationRelativeTo(null)
        isVisible = true
    }
}