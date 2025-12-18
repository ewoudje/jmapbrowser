package com.ewoudje.jmapbrowser

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecureTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(viewModel: AppViewModel) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            AnimatedVisibility(visible = viewModel.session == null) {
                LoginCard(viewModel)
            }

            AnimatedVisibility(visible = viewModel.isSessionReady) {
                SessionView(viewModel.session!!)
            }
        }
    }
}

@Composable
fun LoginCard(viewModel: AppViewModel) = Card {
    val coroutineScope = rememberCoroutineScope()
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        val server = rememberTextFieldState("")
        val username = rememberTextFieldState("")
        val password = rememberTextFieldState("")

        OutlinedTextField(
            state = server,
            placeholder = { Text("https://example.com/") },
            label = { Text("Server") }
        )
        OutlinedTextField(
            state = username,
            label = { Text("Username") }
        )
        SecureTextField(
            state = password,
            label = { Text("Password") },
        )
        Button(onClick = {
            server.setTextAndPlaceCursorAtEnd(viewModel.connect(server.text.toString(), username.text.toString(), password.text.toString(), coroutineScope))
        }) {
            Text("Login")
        }
    }
}