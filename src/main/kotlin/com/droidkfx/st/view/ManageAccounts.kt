package com.droidkfx.st.view

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Frame
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.Vector
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.border.BevelBorder
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

@Suppress("USELESS_CAST") // Cast is required for overload ambiguity
abstract class ManageAccounts : JDialog(null as? Frame, "Manage Accounts", true) {
    private val logger = logger {}

    init {
        logger.trace { "Initializing" }
        minimumSize = Dimension(700, 500)
        add(JPanel(GridBagLayout()).apply {
            add(JPanel(BorderLayout()).apply {
                add(JList(Vector(listOf("Account 1", "Account 2"))), BorderLayout.CENTER)
                add(JPanel().apply {
                    add(JButton("Refresh"))
                    border = EmptyBorder(5, 5, 5, 5)
                }, BorderLayout.SOUTH)
            }, GridBagConstraints().apply {
                gridx = 0
                gridy = 0
                weightx = 1.0
                weighty = 1.0
                fill = GridBagConstraints.BOTH
            })

            add(JPanel(GridBagLayout()).apply {
                border = CompoundBorder(
                    EmptyBorder(0, 5, 5, 5),
                    BevelBorder(BevelBorder.LOWERED)
                )

                add(JLabel("Account ID:"), GridBagConstraints().apply {
                    gridx = 0
                    gridy = 0
                })
                add(JLabel("123456789"), GridBagConstraints().apply {
                    gridx = 1
                    gridy = 0
                })

                add(JLabel("Account Hash:"), GridBagConstraints().apply {
                    gridx = 0
                    gridy = 1
                })
                add(JLabel("123456789"), GridBagConstraints().apply {
                    gridx = 1
                    gridy = 1
                })

                add(JLabel("Position Configuration:"), GridBagConstraints().apply {
                    gridx = 0
                    gridy = 2
                })

                add(
                    JScrollPane(
                        JTable(
                            arrayOf(
                                arrayOf("SPY", "35 %"),
                                arrayOf("VTA", "35 %"),
                                arrayOf("VNI", "15 %"),
                                arrayOf("SFY", "15 %")
                            ),
                            arrayOf("Position", "Allocation")
                        ).apply {

                        }), GridBagConstraints().apply {
                        gridy = 3
                        gridwidth = 2
                        weighty = 1.0
                        weightx = 1.0
                        fill = GridBagConstraints.BOTH
                    })
            }, GridBagConstraints().apply {
                gridx = 1
                gridy = 0
                weightx = 3.0
                weighty = 1.0
                fill = GridBagConstraints.BOTH
            })

            addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent?) {
                    logger.trace { "Dialog is closing" }
                }
            })
            defaultCloseOperation = HIDE_ON_CLOSE
        })
    }

    fun showDialog() {
//        pack()
        setLocationRelativeTo(null)
        isVisible = true
    }
}