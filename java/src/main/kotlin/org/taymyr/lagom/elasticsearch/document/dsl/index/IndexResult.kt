package org.taymyr.lagom.elasticsearch.document.dsl.index

import com.fasterxml.jackson.annotation.JsonProperty

data class IndexResult(
    @JsonProperty("_index") val index: String,
    @JsonProperty("_type") val type: String,
    @JsonProperty("_id") val id: String,
    @JsonProperty("_version") val version: Int,
    val result: String? = null
)