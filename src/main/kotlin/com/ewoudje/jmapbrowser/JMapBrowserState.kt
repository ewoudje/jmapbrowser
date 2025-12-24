package com.ewoudje.jmapbrowser

import com.ewoudje.jmapbrowser.session.JMapAccount
import com.ewoudje.jmapbrowser.session.JMapCapability
import com.ewoudje.jmapbrowser.session.JMapMethodDefinition
import com.ewoudje.jmapbrowser.session.JMapSession
import com.ewoudje.jmapbrowser.session.JMapType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import java.util.*


class JMapBrowserState(
    private val session: JMapSession,
    val scope: CoroutineScope,
) {
    private val capabilityListeners = Collections.newSetFromMap<(JMapCapability?) -> Unit>(WeakHashMap())
    private val accountListeners = Collections.newSetFromMap<(JMapAccount?) -> Unit>(WeakHashMap())

    lateinit var capabilities: List<JMapCapability>
        private set
    lateinit var accounts: List<JMapAccount>
        private set

    var currentCapability: JMapCapability? = null
        set(value) {
            capabilityListeners.forEach { it(value) };
            field = value
        }
    var currentAccount: JMapAccount? = null
        set(value) {
            accountListeners.forEach { it(value) };
            field = value
        }

    suspend fun onUpdate() {
        withContext(scope.coroutineContext) {
            capabilities = session.capabilities.values.toList()
            accounts = session.accounts.values.toList()
        }
    }

    fun select(item: Any) = when (item) {
        is JMapCapability -> currentCapability = item
        is JMapAccount -> currentAccount = item
        is JMapType -> TODO()
        is JMapMethodDefinition -> TODO()
        else -> throw IllegalArgumentException()
    }

    fun addAccountListener(listener: (JMapAccount?) -> Unit) = accountListeners.add(listener)
    fun addCapabilityListener(listener: (JMapCapability?) -> Unit) = capabilityListeners.add(listener)
}