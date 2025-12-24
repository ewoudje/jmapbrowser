package com.ewoudje.jmapbrowser

import com.ewoudje.jmapbrowser.session.JMapAccountCapability
import com.ewoudje.jmapbrowser.session.JMapCapability
import com.ewoudje.jmapbrowser.util.JsonValue
import com.ewoudje.jmapbrowser.util.SimpleEntry
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class OverviewPane(capability: JMapCapability): JPanel() {
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(JLabel(capability.urn))
        buildPane(capability)
    }

    private fun buildPane(capability: JMapCapability) {
        if (capability is JMapAccountCapability) buildPane(capability.capability)

        val myTitle = when (capability) {
            is JMapAccountCapability -> "Account Configuration"
            else -> "Global Configuration"
        }

        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(myTitle),
            BorderFactory.createEmptyBorder(5,5,5,5))
        )
        panel.add(Box.createHorizontalGlue())

        if (capability.configuration.isEmpty()) {
            panel.add(JLabel("No configuration available"))
        } else {
            val xpanel = JPanel()
            xpanel.layout = BoxLayout(xpanel, BoxLayout.Y_AXIS)
            xpanel.alignmentX = LEFT_ALIGNMENT

            for (field in capability.configuration) {
                xpanel.add(SimpleEntry(field.key, JsonValue(field.value)))
            }

            panel.add(xpanel)
        }



        add(panel)
    }
}
