package com.ewoudje.jmapbrowser.util

import com.ewoudje.jmapbrowser.session.FileNode
import com.ewoudje.jmapbrowser.session.JMapAccount
import com.ewoudje.jmapbrowser.session.JMapCapability
import com.ewoudje.jmapbrowser.session.JMapMethodDefinition
import com.ewoudje.jmapbrowser.session.JMapType
import javax.swing.JTree
import javax.swing.tree.TreeModel

class MyTree() : JTree() {
    init {
        isRootVisible = false
    }

    override fun convertValueToText(
        value: Any,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ): String = when (value) {
        is String -> value
        is JMapAccount -> value.name
        is JMapCapability -> value.urn
        is JMapType -> value.name
        is JMapMethodDefinition -> value.name
        is FileNode -> value.name
        else -> value.toString()
    }
}