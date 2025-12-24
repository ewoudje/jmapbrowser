package com.ewoudje.jmapbrowser.session

import kotlinx.serialization.json.JsonElement

class JMapAccount(
    val session: JMapSession,
    val id: JMapId,
    val name: String,
    val isPersonal: Boolean,
    val isReadOnly: Boolean,
    val capabilities: List<JMapAccountCapability>
) {
    constructor(session: JMapSession, id: JMapId, data: JMapAccountData) : this(
        session,
        id,
        data.name,
        data.isPersonal,
        data.isReadOnly,
        data.accountCapabilities.map { (urn, config) ->
            JMapAccountCapability(session.capabilities[urn]!!, config)
        }
    )
}

data class JMapAccountCapability(
    val capability: JMapCapability,
    override val configuration: Map<String, JsonElement>
) : JMapCapability by capability