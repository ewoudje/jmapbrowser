package com.ewoudje.jmapbrowser

import com.ewoudje.jmapbrowser.session.JMapAccount
import com.ewoudje.jmapbrowser.session.JMapCapability
import com.ewoudje.jmapbrowser.session.JMapType
import javax.swing.event.TreeModelListener
import javax.swing.tree.TreeModel
import javax.swing.tree.TreePath

class JMapCapabilityTreeModel(
    state: JMapBrowserState,
    acc: JMapAccount?
) : TreeModel {
    private val root = Any()

    override fun getRoot(): Any = root

    private val capabilities = acc?.capabilities ?: state.capabilities

    override fun getChild(parent: Any, index: Int): Any = when (parent) {
        root -> capabilities[index]
        is JMapCapability -> parent.knowTypes[index]
        is JMapType -> parent.methods[index]
        else -> throw IllegalArgumentException("Unexpected parent")
    }

    override fun getChildCount(parent: Any): Int =  when (parent) {
        root -> capabilities.size
        is JMapCapability -> parent.knowTypes.size
        is JMapType -> parent.methods.size
        else -> throw IllegalArgumentException("Unexpected parent")
    }

    override fun isLeaf(node: Any): Boolean = when (node) {
        root -> false
        is JMapCapability -> node.knowTypes.isEmpty()
        is JMapType -> node.methods.isEmpty()
        else -> true
    }

    override fun valueForPathChanged(path: TreePath, newValue: Any) =
        throw UnsupportedOperationException()

    override fun getIndexOfChild(parent: Any, child: Any): Int = when (parent) {
        root -> capabilities.indexOf(child)
        is JMapCapability -> parent.knowTypes.indexOf(child)
        is JMapType -> parent.methods.indexOf(child)
        else -> throw IllegalArgumentException("Unexpected parent")
    }

    override fun addTreeModelListener(l: TreeModelListener) {

    }

    override fun removeTreeModelListener(l: TreeModelListener) {

    }
}