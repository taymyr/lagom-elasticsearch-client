package org.taymyr.lagom.elasticsearch.document.dsl

import com.fasterxml.jackson.annotation.JsonProperty

abstract class Document<T> {
    @get:JsonProperty("_source")
    abstract val source: T
    @JsonProperty("_index") val index: String = ""
    @JsonProperty("_type") val type: String = ""
    @JsonProperty("_id") val id: String = ""
    @JsonProperty("_version") val version: Int = -1
}