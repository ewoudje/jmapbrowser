package com.ewoudje.jmapbrowser

import com.ewoudje.jmapbrowser.session.FileNode
import com.ewoudje.jmapbrowser.session.FileNodeCapability
import com.ewoudje.jmapbrowser.session.JMapAccount
import com.ewoudje.jmapbrowser.session.JMapId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import javax.swing.event.TreeModelEvent
import javax.swing.event.TreeModelListener
import javax.swing.tree.TreeModel
import javax.swing.tree.TreePath

@OptIn(ExperimentalCoroutinesApi::class)
class FileNodeTreeModel(
    val account: JMapAccount,
    val capability: FileNodeCapability
) : TreeModel {
    private val listeners = mutableListOf<TreeModelListener>()
    private val root = FileNode(
        "__root__",
        name = "fake root",
    )
    private val map = mutableMapOf<JMapId, MutableList<FileNode>>()

    init {
        account.session.scope.launch {
            val nodes = capability.get(account)

            for (node in nodes) {
                map.getOrPut(node.parentId ?: "__root__") { mutableListOf() }.add(node)
            }

            withContext(Dispatchers.Swing) {
                listeners.forEach { it.treeStructureChanged(TreeModelEvent(this, arrayOf(root))) }
            }
        }
    }

    override fun getRoot(): Any? = root

    override fun getChild(parent: Any?, index: Int): Any? {
        val parent = parent as FileNode

        return map[parent.id]?.get(index)
    }

    override fun getChildCount(parent: Any?): Int {
        val parent = parent as FileNode

        return map[parent.id]?.size ?: 0
    }

    override fun isLeaf(node: Any?): Boolean {
        val node = node as FileNode

        return map[node.id] == null
    }

    override fun valueForPathChanged(path: TreePath?, newValue: Any?)
        = TODO()

    override fun getIndexOfChild(parent: Any?, child: Any?): Int = 0

    override fun addTreeModelListener(l: TreeModelListener) {
        listeners.add(l)
    }

    override fun removeTreeModelListener(l: TreeModelListener) {
        listeners.remove(l)
    }
}