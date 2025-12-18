package com.ewoudje.jmapbrowser.session

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject


class JMapSession(
    private val client: HttpClient,
    private val sessionUrl: String
): Closeable {
    private var tickJob: Job? = null
    private lateinit var invoker: JMapInvoker
    lateinit var sessionData: JMapSessionData
        private set

    lateinit var capabilities: Map<Urn, JMapCapability>
        private set

    lateinit var accounts: Map<JMapId, JMapAccount>
        private set

    suspend fun init(onUpdate: () -> Unit) {
        updateSession()
        invoker = JMapInvoker(client, sessionData.apiUrl)
        onUpdate()

        tickJob = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                delay(500)
                val newState = invoker.sendRequest()
                if (newState != null && newState != sessionData.state) {
                    updateSession()
                    onUpdate()
                }
            }
        }
    }

    suspend fun call() {

    }

    private suspend fun updateSession() {
        val response = client.get(sessionUrl)
        if (!response.status.isSuccess()) throw IllegalStateException(response.status.description)

        sessionData = response.body()
        capabilities = sessionData.capabilities.map { (key, value) -> key to parseCapability(key, value) }.toMap()
        accounts = sessionData.accounts.map { it.key to JMapAccount(this, it.key, it.value) }.toMap()
    }

    private fun parseCapability(
        key: Urn,
        value: JsonObject
    ): JMapCapability = UnknownJMapCapability(key, value)

    override fun close() {
        tickJob?.cancel()
        tickJob = null
        client.close()
    }
}