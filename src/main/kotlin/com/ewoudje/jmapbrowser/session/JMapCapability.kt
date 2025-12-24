package com.ewoudje.jmapbrowser.session

import kotlinx.serialization.json.JsonElement

interface JMapCapability {
    val urn: Urn
    val configuration: Map<String, JsonElement>
    val knowTypes: List<JMapType>
}

data class StandardJMapCapability(
    override val urn: Urn,
    override val configuration: Map<String, JsonElement>,
    override val knowTypes: List<JMapType>
) : JMapCapability