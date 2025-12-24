package com.ewoudje.com.ewoudje.com.ewoudje.jmapbrowser

import com.ewoudje.jmapbrowser.JMapBrowserFrame
import com.ewoudje.jmapbrowser.JMapBrowserState
import com.ewoudje.jmapbrowser.session.JMapSession
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import org.slf4j.LoggerFactory
import javax.swing.JFrame

private val logger = LoggerFactory.getLogger("Main")

fun main(args: Array<String>) {

    if (args.size == 3) {
        buildBrowser(args[0], args[1], args[2])
    }
}

private fun buildBrowser(server: String, username: String, password: String) {
    val client = HttpClient(CIO) {
        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(username = username, password = password)
                }

                sendWithoutRequest { _ -> true }
            }
        }

        install(ContentNegotiation) {
            json()
        }
    }

    val url = URLBuilder(server)
    if (url.host.isEmpty()) url.host = server

    url.path()
    if (url.protocolOrNull == null)
        url.protocol = URLProtocol.HTTP

    logger.info("Connecting to ${url.buildString()} as $username")

    url.path(".well-known", "jmap")

    val default = CoroutineScope(Dispatchers.Default)
    val swingScope = CoroutineScope(Dispatchers.Swing)

    val session = JMapSession(
        client,
        url.buildString(),
        default
    )

    val state = JMapBrowserState(
        session,
        swingScope
    )

    swingScope.launch {
        var lastFrame: JFrame? = null
        session.init {
            lastFrame?.dispose()
            state.onUpdate()
            lastFrame = JMapBrowserFrame(state)
        }
    }
}