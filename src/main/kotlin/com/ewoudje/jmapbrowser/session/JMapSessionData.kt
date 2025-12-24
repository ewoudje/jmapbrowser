package com.ewoudje.jmapbrowser.session

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import kotlinx.serialization.json.JsonObject

typealias JMapId = String
typealias Urn = String

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class JMapSessionData(
    val capabilities: Map<Urn, JsonObject>,
    val accounts: Map<JMapId, JMapAccountData>,
    val primaryAccounts: Map<Urn, JMapId>,
    val username: String,
    val apiUrl: String,
    val downloadUrl: String,
    val uploadUrl: String,
    val eventSourceUrl: String,
    val state: String
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class JMapAccountData(
    val name: String,
    val isPersonal: Boolean,
    val isReadOnly: Boolean,
    val accountCapabilities: Map<Urn, JsonObject>,
)