package com.ewoudje.jmapbrowser

import javax.swing.ComboBoxModel
import javax.swing.event.ListDataListener

class JMapAccountDropdownModel(val state: JMapBrowserState) : ComboBoxModel<String> {
    override fun setSelectedItem(anItem: Any?) {
        state.currentAccount = if (anItem == "None") null else state.accounts.find {
            it.name == anItem.toString()
        }
    }

    override fun getSelectedItem(): Any? = state.currentAccount?.name ?: "None"
    override fun getSize(): Int = state.accounts.size + 1
    override fun getElementAt(index: Int): String? {
        if (index == 0) return "None"
        return state.accounts.getOrNull(index - 1)?.name
    }

    override fun addListDataListener(l: ListDataListener?) {
    }

    override fun removeListDataListener(l: ListDataListener?) {
    }
}
