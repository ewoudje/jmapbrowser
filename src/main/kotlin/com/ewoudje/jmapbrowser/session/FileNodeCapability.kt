package com.ewoudje.jmapbrowser.session

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.util.Properties

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class FileNode(
    val id: JMapId,
    val parentId: JMapId? = null,
    val blobId: JMapId? = null,
    val size: Int? = null,
    val name: String,
    val type: String? = null,
    val created: String? = null,
    val modified: String? = null,
    val accessed: String? = null,
    val executable: Boolean = false,
    val isSubscribed: Boolean = false,
)

class FileNodeCapability(override val configuration: Map<String, JsonElement>) : JMapCapability {
    override val urn: Urn = "urn:ietf:params:jmap:filenode"
    override val knowTypes: List<JMapType> = listOf(
        type("FileNode") {
            get {}
            set {
                optional("onDestroyRemoveChildren")
            }
            changes {}
            query {
                optional("depth")
            }
            queryChanges {}
        }
    )

    suspend fun get(account: JMapAccount, ids: List<JMapId>? = null, properties: List<String>? = null): List<FileNode> {
        val result = account.session.call(this, "FileNode/get", buildMap {
            put("accountId", JsonPrimitive(account.id))
            if (ids != null) put("ids", JsonArray(ids.map { JsonPrimitive(it) }))
            if (properties != null) put("properties", JsonArray(properties.map { JsonPrimitive(it) }))
        })

        if (result.size != 1) throw IllegalStateException()
        val serializer = JMapGetResult.serializer(FileNode.serializer())
        val getResult = Json.decodeFromJsonElement(serializer, JsonObject(result[0].arguments))

        return getResult.list
    }
}