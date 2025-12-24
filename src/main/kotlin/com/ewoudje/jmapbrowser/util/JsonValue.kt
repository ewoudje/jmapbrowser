package com.ewoudje.jmapbrowser.util

import kotlinx.serialization.json.JsonElement
import javax.swing.JLabel

class JsonValue(value: JsonElement) : JLabel(value.toString()) {
}