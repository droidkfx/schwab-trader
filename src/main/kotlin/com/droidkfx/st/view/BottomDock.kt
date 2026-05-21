package com.droidkfx.st.view

import java.awt.CardLayout
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * A dockable bottom panel that holds named expandable panels behind a tab strip.
 *
 * The tab strip ([tabStrip]) and content area ([contentPanel]) are separate components
 * so the caller can place them independently — [tabStrip] in an always-visible SOUTH slot
 * and [contentPanel] as the bottom of a JSplitPane.
 *
 * Clicking a tab expands its panel into the content area; clicking the active tab
 * collapses it. The parent JSplitPane is notified via [onContentVisibilityChanged].
 */
class BottomDock(
    private val onContentVisibilityChanged: (visible: Boolean) -> Unit,
) {
    companion object {
        /** Approximate minimum height of the orders panel: filter bar (~30px) + 2 table rows (~48px). */
        const val MIN_CONTENT_HEIGHT = 78
    }

    private val cardLayout = CardLayout()

    /** The expandable content area — place as the bottom component of a JSplitPane.
     *  Returns a zero-preferred size when invisible, so pack() ignores it. */
    val contentPanel = object : JPanel(cardLayout) {
        override fun getPreferredSize(): Dimension =
            if (isVisible) super.getPreferredSize() else Dimension(0, 0)
    }.apply { isVisible = false }

    /** The always-visible tab strip — place in the SOUTH of the containing panel. */
    val tabStrip = JPanel(FlowLayout(FlowLayout.LEFT, 4, 2))

    private val tabButtons = mutableMapOf<String, JButton>()
    private var activeTab: String? = null

    /** Register a named panel as a dock tab. Must be called before the dock is shown. */
    fun register(label: String, panel: JComponent) {
        contentPanel.add(panel, label)

        val button = JButton(label).apply {
            isFocusPainted = false
            isBorderPainted = false
            isContentAreaFilled = false
            addActionListener { onTabClicked(label) }
        }
        tabButtons[label] = button
        tabStrip.add(button)
    }

    private fun onTabClicked(label: String) {
        if (activeTab == label && contentPanel.isVisible) {
            // Collapse
            contentPanel.minimumSize = Dimension(0, 0)
            contentPanel.isVisible = false
            activeTab = null
            tabButtons.values.forEach { it.isContentAreaFilled = false }
            onContentVisibilityChanged(false)
        } else {
            // Expand (possibly switching tab)
            cardLayout.show(contentPanel, label)
            contentPanel.minimumSize = Dimension(0, MIN_CONTENT_HEIGHT)
            contentPanel.isVisible = true
            activeTab = label
            tabButtons.values.forEach { it.isContentAreaFilled = false }
            tabButtons[label]?.isContentAreaFilled = true
            onContentVisibilityChanged(true)
        }
    }
}
