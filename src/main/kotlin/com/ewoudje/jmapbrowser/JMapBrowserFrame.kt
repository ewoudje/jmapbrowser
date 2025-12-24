package com.ewoudje.jmapbrowser

import com.ewoudje.jmapbrowser.session.JMapAccount
import com.ewoudje.jmapbrowser.util.MyTree
import java.awt.BorderLayout
import javax.swing.BoxLayout
import javax.swing.JComboBox
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JSplitPane

class JMapBrowserFrame(
    private val state: JMapBrowserState
) : JFrame("JMapBrowser") {
    val accListener: (JMapAccount?) -> Unit = { acc -> capabilityTree.model = JMapCapabilityTreeModel(state, acc) }
    val capabilityTree = MyTree()
    val tabs = JMapBrowserView(state)
    val pane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, capabilityTree, tabs)
    val top = JPanel().apply { layout = BoxLayout(this, BoxLayout.X_AXIS) }
    val accountDropdown = JComboBox(JMapAccountDropdownModel(state)).apply { top.add(this) }

    init {
        capabilityTree.addTreeSelectionListener { state.select(it.path.lastPathComponent) }
        accListener.invoke(null)

        state.addAccountListener(accListener)
        state.currentCapability = state.capabilities.first()

        add(top, BorderLayout.PAGE_START)
        add(pane, BorderLayout.CENTER)
        pack()
        defaultCloseOperation = EXIT_ON_CLOSE
        isVisible = true
    }
}