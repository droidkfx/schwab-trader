package com.droidkfx.st.view

import java.awt.BasicStroke
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Insets
import java.awt.RenderingHints
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JPopupMenu
import javax.swing.JScrollPane
import javax.swing.JSeparator
import javax.swing.ScrollPaneConstants
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.border.AbstractBorder
import javax.swing.event.PopupMenuEvent
import javax.swing.event.PopupMenuListener

class MultiSelectDropDown<T>(
    private val items: List<T>,
    private val labelOf: (T) -> String,
    initialSelection: Set<T> = items.toSet(),
    private val onSelectionChanged: (Set<T>) -> Unit,
) : JPanel(BorderLayout()) {

    private val selectedItems: MutableSet<T> = initialSelection.filter { it in items }.toMutableSet()
    private val popup = JPopupMenu()
    private var popupJustClosed = false

    internal val checkBoxItems: List<Pair<T, JCheckBox>>
    internal val allCheckBox: JCheckBox
    internal val textLabel = JLabel().apply {
        // Match ComboBox.padding: top=2, left=6, bottom=2, right=6
        border = BorderFactory.createEmptyBorder(2, 6, 2, 6)
        isOpaque = false
    }

    val selection: Set<T> get() = selectedItems.toSet()

    /** Current display text — mirrors textLabel; exposed so tests don't reach into internals. */
    val text: String get() = textLabel.text ?: ""

    init {
        isOpaque = true
        border = DropDownBorder()

        val arrowPanel = ArrowPanel()

        add(textLabel, BorderLayout.CENTER)
        add(arrowPanel, BorderLayout.EAST)

        // Phase 1: individual checkboxes (listeners added in phase 3)
        checkBoxItems = items.map { item ->
            item to JCheckBox(labelOf(item), item in selectedItems).apply { isOpaque = false }
        }

        // Phase 2: "All" master checkbox
        allCheckBox = JCheckBox("All", selectedItems.size == items.size).apply { isOpaque = false }

        // Phase 3: wire listeners
        allCheckBox.addActionListener {
            if (allCheckBox.isSelected) {
                selectedItems.addAll(items)
                checkBoxItems.forEach { (_, cb) -> cb.isSelected = true }
            } else {
                selectedItems.clear()
                checkBoxItems.forEach { (_, cb) -> cb.isSelected = false }
            }
            updateLabel()
            onSelectionChanged(selectedItems.toSet())
        }
        checkBoxItems.forEach { (item, checkBox) ->
            checkBox.addActionListener {
                if (checkBox.isSelected) selectedItems.add(item) else selectedItems.remove(item)
                allCheckBox.isSelected = selectedItems.size == items.size
                updateLabel()
                onSelectionChanged(selectedItems.toSet())
            }
        }

        // Build popup content
        val panel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createEmptyBorder(2, 4, 2, 4)
            add(allCheckBox)
            add(JSeparator().apply { maximumSize = Dimension(Int.MAX_VALUE, 6) })
            checkBoxItems.forEach { (_, cb) -> add(cb) }
        }

        val scrollPane = JScrollPane(panel).apply {
            border = BorderFactory.createEmptyBorder()
            horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
            verticalScrollBarPolicy = if (items.size > MAX_VISIBLE_ITEMS)
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
            else
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER
        }

        popup.addPopupMenuListener(object : PopupMenuListener {
            private var sized = false

            override fun popupMenuWillBecomeVisible(e: PopupMenuEvent) {
                popupJustClosed = false
                // Cap height to MAX_VISIBLE_ITEMS when the list is long
                if (!sized && items.size > MAX_VISIBLE_ITEMS) {
                    sized = true
                    val ph = panel.preferredSize
                    val rowH = ph.height / (items.size + 1)   // +1 for All row
                    val sbW = scrollPane.verticalScrollBar.preferredSize.width
                    scrollPane.preferredSize = Dimension(
                        ph.width + sbW + 4,
                        rowH * (MAX_VISIBLE_ITEMS + 1) + 8   // +1 for All, +8 border
                    )
                    (e.source as JPopupMenu).pack()
                }
                // Ensure popup is at least as wide as the button
                SwingUtilities.invokeLater {
                    val btn = this@MultiSelectDropDown
                    if (popup.width < btn.width) popup.setPopupSize(btn.width, popup.height)
                }
            }

            override fun popupMenuWillBecomeInvisible(e: PopupMenuEvent) {
                // Flag stays true until after the mouse-press action fires, then resets
                popupJustClosed = true
                SwingUtilities.invokeLater { popupJustClosed = false }
            }

            override fun popupMenuCanceled(e: PopupMenuEvent) {}
        })

        popup.add(scrollPane)

        updateLabel()

        // Fixed width: measure the widest possible label text once at construction time
        val fm = getFontMetrics(font ?: Font("Dialog", Font.PLAIN, 12))
        val widestText = (items.map { labelOf(it) } + listOf("All", "None", "${items.size} selected"))
            .maxOf { fm.stringWidth(it) }
        val ps = preferredSize
        preferredSize = Dimension(widestText + FIXED_WIDTH_PADDING, ps.height.coerceAtLeast(MIN_HEIGHT))
        minimumSize = preferredSize
        maximumSize = Dimension(preferredSize.width, Short.MAX_VALUE.toInt())

        // Toggle popup on click; second click closes via popupJustClosed flag
        val mouseHandler = object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (!popupJustClosed) popup.show(this@MultiSelectDropDown, 0, height)
            }
        }
        addMouseListener(mouseHandler)
        textLabel.addMouseListener(mouseHandler)
        arrowPanel.addMouseListener(mouseHandler)
    }

    private fun updateLabel() {
        textLabel.text = when {
            selectedItems.size == items.size -> "All"
            selectedItems.isEmpty() -> "None"
            selectedItems.size <= 2 -> items.filter { it in selectedItems }.joinToString(", ") { labelOf(it) }
            else -> "${selectedItems.size} selected"
        }
    }

    override fun paintComponent(g: Graphics) {
        g.color = UIManager.getColor("ComboBox.background") ?: Color.WHITE
        g.fillRect(0, 0, width, height)
    }

    private class ArrowPanel : JPanel() {
        init {
            isOpaque = false
            preferredSize = Dimension(ARROW_AREA_WIDTH, 0)
        }

        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            val g2 = g.create() as Graphics2D
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)

                val arrowColor = UIManager.getColor("ComboBox.buttonArrowColor")
                    ?: UIManager.getColor("Label.foreground")
                    ?: Color.DARK_GRAY
                g2.color = arrowColor
                g2.stroke = BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)

                // Downward chevron centred in the panel, proportions matching FlatLaf's FlatArrowIcon
                val cx = width / 2f
                val cy = height / 2f
                val aw = 4f    // half-width of chevron arms
                val ah = 2.5f  // half-height of chevron arms

                val path = java.awt.geom.Path2D.Float()
                path.moveTo(cx - aw, cy - ah)
                path.lineTo(cx, cy + ah)
                path.lineTo(cx + aw, cy - ah)
                g2.draw(path)
            } finally {
                g2.dispose()
            }
        }
    }

    /** Renders a rounded border using the same color and arc as FlatLaf's Component border. */
    private class DropDownBorder : AbstractBorder() {
        override fun paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
            val g2 = g.create() as Graphics2D
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                g2.color = UIManager.getColor("Component.borderColor") ?: Color.GRAY
                val arc = UIManager.getInt("Component.arc").coerceAtLeast(3)
                g2.drawRoundRect(x, y, width - 1, height - 1, arc, arc)
            } finally {
                g2.dispose()
            }
        }

        override fun getBorderInsets(c: Component): Insets = Insets(1, 1, 1, 1)
        override fun isBorderOpaque(): Boolean = false
    }

    companion object {
        private const val MAX_VISIBLE_ITEMS = 5
        private const val ARROW_AREA_WIDTH = 16     // matches FlatComboBoxButton preferred width
        private const val FIXED_WIDTH_PADDING = 32   // border(2) + text-padding(12) + arrow(16) + slack(2)
        private const val MIN_HEIGHT = 22            // matches JComboBox.preferredSize.height under FlatLaf
    }
}
