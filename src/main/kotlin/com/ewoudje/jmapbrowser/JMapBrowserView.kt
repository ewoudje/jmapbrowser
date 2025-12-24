package com.ewoudje.jmapbrowser

import com.ewoudje.jmapbrowser.session.FileNode
import com.ewoudje.jmapbrowser.session.FileNodeCapability
import com.ewoudje.jmapbrowser.session.JMapAccountCapability
import com.ewoudje.jmapbrowser.session.JMapCapability
import com.ewoudje.jmapbrowser.util.MyTree
import javax.swing.JLabel
import javax.swing.JTabbedPane

class JMapBrowserView(val state: JMapBrowserState) : JTabbedPane(), (JMapCapability?) -> Unit {

    init {
        invoke(state.currentCapability)
        state.addCapabilityListener(this)
    }

    override fun invoke(capability: JMapCapability?) {
        val capability = capability ?: return

        for (i in 0 until this.tabCount) {
            this.removeTabAt(0)
        }

        addTab("Overview", OverviewPane(capability))

        if (capability is JMapAccountCapability) {
            when (capability.capability) {
                is FileNodeCapability -> {
                    addTab(
                        "Files",
                        MyTree().apply { model = FileNodeTreeModel(state.currentAccount!!, capability.capability) }
                    )
                }
            }
        }
    }
}