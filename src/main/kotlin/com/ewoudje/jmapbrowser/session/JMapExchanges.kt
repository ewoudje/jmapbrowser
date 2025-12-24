package com.ewoudje.jmapbrowser.session

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.collections.get

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class JMapRequest(val using: Set<Urn>, val methodCalls: List<JMapInvocation>, val createdIds: Map<JMapId,JMapId>? = null)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class JMapResponse(val methodResponses: List<JMapInvocation>, val sessionState: String, val createdIds: Map<JMapId,JMapId>? = null)

@Serializable(with = JMapCallSerializer::class)
data class JMapInvocation(val name: String, val arguments: Map<String, JsonElement>, val callId: String)

object JMapCallSerializer : KSerializer<JMapInvocation> {
    private val delegateSerializer = JsonArray.serializer()
    // Serial names of descriptors should be unique, so we cannot use ColorSurrogate.serializer().descriptor directly
    override val descriptor: SerialDescriptor = SerialDescriptor("com.ewoudje.jmapbrowser.JMapCall", delegateSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: JMapInvocation) {
        delegateSerializer.serialize(encoder, JsonArray(listOf(JsonPrimitive(value.name), JsonObject(value.arguments), JsonPrimitive(value.callId))))
    }

    override fun deserialize(decoder: Decoder): JMapInvocation {
        val array = delegateSerializer.deserialize(decoder)
        return JMapInvocation(array[0].jsonPrimitive.content, array[1].jsonObject as Map<String, JsonObject>, array[2].jsonPrimitive.content)
    }
}