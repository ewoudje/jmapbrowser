package com.ewoudje.jmapbrowser

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.Dispatchers

fun main() = application {
    val model = AppViewModel()
    Window(
        onCloseRequest = ::exitApplication,
        title = "jmapbrowser",
    ) {
        App(model)
    }
}