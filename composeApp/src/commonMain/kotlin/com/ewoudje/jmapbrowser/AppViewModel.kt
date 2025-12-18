package com.ewoudje.jmapbrowser
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ewoudje.jmapbrowser.session.JMapSession
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {
    var isSessionReady by mutableStateOf(false)
        private set
    var session: SessionViewModel? by mutableStateOf(null)
        private set

    fun connect(server: String, username: String, password: String, uiScope: CoroutineScope) {
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

        session = SessionViewModel(JMapSession(client, "$server.well-known/jmap"))
        viewModelScope.launch {
            session?.init()
            isSessionReady = true
        }
    }
}