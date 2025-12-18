package com.ewoudje.jmapbrowser.session

import kotlinx.serialization.json.JsonElement

interface JMapCapability {
    val urn: Urn
}

data class UnknownJMapCapability(override val urn: Urn, val configuration: Map<String, JsonElement>) : JMapCapability