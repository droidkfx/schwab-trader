package com.droidkfx.st.view.about

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Font
import java.awt.Frame
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Image
import java.awt.Insets
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

@Suppress("USELESS_CAST") // Cast is required for overload ambiguity on Frame
class AboutDialog(vm: AboutViewModel) : JDialog(null as? Frame, "About Schwab Trader", true) {
    init {
        layout = BorderLayout()

        val logoLabel = JLabel(appLogo(), SwingConstants.CENTER)
        logoLabel.border = BorderFactory.createEmptyBorder(16, 0, 12, 0)
        add(logoLabel, BorderLayout.NORTH)

        val content = JPanel(GridBagLayout())
        content.border = BorderFactory.createEmptyBorder(0, 24, 8, 24)
        val rows: List<Row> =
            listOf(
                Row.Entry("Schwab Trader", vm.appVersion, labelBold = true),
                Row.Entry("Git hash:", vm.gitHash),
                Row.Entry("Branch:", vm.gitBranch),
                Row.Entry("Built:", vm.buildTime),
                Row.Spacer,
                Row.Entry("JVM:", "${vm.jvmVersion} (${vm.jvmVendor})"),
                Row.Entry("OS:", "${vm.osName} ${vm.osVersion} (${vm.osArch})"),
            )
        content.addRows(rows)
        add(content, BorderLayout.CENTER)

        val closeBtn = JButton("Close")
        closeBtn.addActionListener { dispose() }
        val btnPanel = JPanel()
        btnPanel.border = BorderFactory.createEmptyBorder(4, 0, 12, 0)
        btnPanel.add(closeBtn)
        add(btnPanel, BorderLayout.SOUTH)

        pack()
        val packed = size
        minimumSize = packed
        maximumSize = packed
        isResizable = false
    }

    fun showDialog() {
        setLocationRelativeTo(null)
        isVisible = true
    }
}

private const val LOGO_SIZE = 64

private fun appLogo(): ImageIcon {
    val url = AboutDialog::class.java.getResource("/com/droidkfx/st/view/AppIcon.png")
    if (url != null) {
        val scaled = ImageIcon(url).image.getScaledInstance(LOGO_SIZE, LOGO_SIZE, Image.SCALE_SMOOTH)
        return ImageIcon(scaled)
    }
    return placeholderLogo()
}

private fun placeholderLogo(): ImageIcon {
    val img = BufferedImage(LOGO_SIZE, LOGO_SIZE, BufferedImage.TYPE_INT_ARGB)
    val g = img.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.color = Color(40, 80, 60)
    g.fillRoundRect(0, 0, LOGO_SIZE, LOGO_SIZE, 16, 16)
    g.color = Color(120, 200, 150)
    g.font = Font(Font.SANS_SERIF, Font.BOLD, 18)
    val text = "ST"
    val fm = g.fontMetrics
    g.drawString(text, (LOGO_SIZE - fm.stringWidth(text)) / 2, (LOGO_SIZE + fm.ascent - fm.descent) / 2)
    g.dispose()
    return ImageIcon(img)
}

private sealed class Row {
    data class Entry(val label: String, val value: String, val labelBold: Boolean = false) : Row()

    data object Spacer : Row()
}

private fun JPanel.addRows(rows: List<Row>) {
    val gc =
        GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            insets = Insets(2, 4, 2, 4)
        }
    rows.forEachIndexed { rowIndex, row ->
        gc.gridy = rowIndex
        when (row) {
            is Row.Entry -> {
                gc.gridwidth = 1

                gc.gridx = 0
                gc.weightx = 0.0
                val lbl = JLabel(row.label, SwingConstants.RIGHT)
                if (row.labelBold) lbl.font = lbl.font.deriveFont(Font.BOLD)
                add(lbl, gc)

                gc.gridx = 1
                gc.weightx = 1.0
                add(JLabel(row.value, SwingConstants.LEFT), gc)
            }

            Row.Spacer -> {
                gc.gridx = 0
                gc.gridwidth = 2
                gc.weightx = 0.0
                add(JLabel(" "), gc)
            }
        }
    }
}
