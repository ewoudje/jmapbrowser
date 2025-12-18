package com.ewoudje.jmapbrowser.session

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonObject
import kotlin.collections.mutableListOf
import kotlin.io.encoding.Base64
import kotlin.random.Random
class JMapInvoker(
    private val client: HttpClient,
    private val url: String,
) : Closeable {

    private val mutex = Mutex()
    private val using = mutableSetOf<Urn>()
    private val calls = mutableListOf<Triple<String,  Map<String, JsonObject>, CompletableDeferred<JMapCallResult>>>()

    suspend fun doCall(method: String, arguments: Map<String, JsonObject>, usingUrn: String): JMapCallResult {
        val future = CompletableDeferred<JMapCallResult>()
        mutex.withLock {
            calls.add(Triple(method, arguments, future))
            using.add(usingUrn)
        }

        return future.await()
    }

    /**
     * returns session state
     */
    suspend fun sendRequest(): String? = mutex.withLock {
        if (calls.isEmpty()) return@withLock null

        val resultMap = mutableMapOf<String, Pair<MutableList<JMapInvocation>, CompletableDeferred<JMapCallResult>>>()
        val callList = mutableListOf<JMapInvocation>()
        for ((method, args, deffer) in calls) {
            val id = randomId()
            callList.add(JMapInvocation(method, args, id))
            resultMap[id] = Pair(mutableListOf(), deffer)
        }

        val request = JMapRequest(using, callList)

        val result = client.post(url) {
            headers {
                append(HttpHeaders.Accept, "application/json")
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        calls.clear()
        using.clear()

        val response: JMapResponse = result.body()

        for (r in response.methodResponses) {
            val (list, _) = resultMap[r.callId] ?: throw IllegalArgumentException("Unknown call id ${r.callId}")
            list.add(r)
        }

        for ((lst, deferred) in resultMap.values) {
            deferred.complete(JMapCallResult(lst))
        }

        response.sessionState
    }

    private fun randomId(): String = Base64.encode(Random.nextBytes(16))

    override fun close() {
        client.close()
    }
}