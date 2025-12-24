package com.ewoudje.jmapbrowser.util

import java.awt.Component
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class SimpleEntry(name: String, value: JComponent) : JPanel() {
    init {
        layout = BoxLayout(this, BoxLayout.X_AXIS)
        add(JLabel(name))
        add(Box.createHorizontalGlue())
        add(value)
    }
}