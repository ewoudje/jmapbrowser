package com.ewoudje.jmapbrowser

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ewoudje.jmapbrowser.session.JMapAccount
import com.ewoudje.jmapbrowser.session.JMapCapability
import com.ewoudje.jmapbrowser.session.JMapSession
import kotlinx.coroutines.launch

class SessionViewModel(
    private val session: JMapSession
) : ViewModel() {
    var username by mutableStateOf("")
        private set
    var capabilities: Collection<JMapCapability> by mutableStateOf(emptySet())
        private set
    var accounts: Collection<JMapAccount> by mutableStateOf(emptySet())
        private set

    var visibleCapability: JMapCapability? by mutableStateOf(null)
    var selectedAccount: JMapAccount? by mutableStateOf(null)

    private fun update() {
        viewModelScope.launch {
            username = session.sessionData.username
            visibleCapability?.let { visibleCapability = session.capabilities[it.urn] }
            capabilities = session.capabilities.values
            accounts = session.accounts.values
        }
    }

    suspend fun init() {
        session.init(this::update)
    }
}