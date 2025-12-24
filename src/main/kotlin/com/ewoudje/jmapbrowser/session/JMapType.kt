package com.ewoudje.jmapbrowser.session

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import kotlin.collections.listOf
import kotlin.to


data class JMapType(val name: String, val methods: List<JMapMethodDefinition>)
data class JMapMethodDefinition(val name: String, val requiredArguments: List<String>, val optionalArguments: List<String>)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class JMapGetResult<T>(
    val accountId: JMapId,
    val state: String,
    val list: List<T>,
    val notFound: List<JMapId>,
)

val jmapTypesSpec: Map<Urn, (Map<String, JsonElement>) -> JMapCapability> = mapOf(
    "urn:ietf:params:jmap:core" to { StandardJMapCapability("urn:ietf:params:jmap:core", it,
        listOf(type("Core") {
            method("echo") {
                any()
            }
        })
    ) },
    "urn:ietf:params:jmap:filenode" to { FileNodeCapability(it) }
)

interface TypeBuilder {
    fun method(name: String, builder: MethodBuilder.() -> Unit)

    fun get(builder: MethodBuilder.() -> Unit) {
        method("get") {
            required("accountId")
            optional("ids")
            optional("properties")
            builder()
        }
    }

    fun set(builder: MethodBuilder.() -> Unit) {
        method("set") {
            required("accountId")
            optional("ifInState")
            optional("create")
            optional("update")
            optional("destroy")

            builder()
        }
    }

    fun changes(builder: MethodBuilder.() -> Unit) {
        method("changes") {
            required("accountId")
            required("sinceState")
            optional("maxChanges")

            builder()
        }
    }

    fun copy(builder: MethodBuilder.() -> Unit) {
        method("copy") {
            required("fromAccountId")
            optional("ifFromInState")
            required("accountId")
            optional("ifInState")
            required("create")
            optional("onSuccessDestroyOriginal")
            optional("destroyFromIfInState")

            builder()
        }
    }

    fun query(builder: MethodBuilder.() -> Unit) {
        method("query") {
            required("accountId")
            optional("filter")
            optional("sort")
            optional("position")
            optional("anchor")
            optional("anchorOffset")
            optional("limit")
            optional("calculateTotal")

            builder()
        }
    }

    fun queryChanges(builder: MethodBuilder.() -> Unit) {
        method("queryChanges") {
            required("accountId")
            optional("filter")
            optional("sort")
            required("sinceQueryState")
            optional("maxChanges")
            optional("upToId")
            optional("calculateTotal")

            builder()
        }
    }
}
interface MethodBuilder {
    fun any()
    fun optional(name: String)
    fun required(name: String)
}

fun type(name: String, builder: TypeBuilder.() -> Unit): JMapType {
    val methods = mutableListOf<JMapMethodDefinition>()
    (object : TypeBuilder {
        override fun method(name: String, builder: MethodBuilder.() -> Unit) {
            val requiredArguments = mutableListOf<String>()
            val optionalArguments = mutableListOf<String>()

            (object : MethodBuilder {
                override fun any() {}

                override fun optional(name: String) {
                    optionalArguments.add(name)
                }

                override fun required(name: String) {
                    requiredArguments.add(name)
                }

            }).builder()

            methods.add(JMapMethodDefinition(name, requiredArguments, optionalArguments))
        }
    }).builder()

    return JMapType(name, methods)
}