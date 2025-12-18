package com.ewoudje.jmapbrowser

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val model = AppViewModel()
    ComposeViewport {
        App(model)
    }
}