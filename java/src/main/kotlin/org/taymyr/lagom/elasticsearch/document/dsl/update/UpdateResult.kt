package org.taymyr.lagom.elasticsearch.document.dsl.update

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateResult(
    @JsonProperty("_index") val index: String,
    @JsonProperty("_type") val type: String,
    @JsonProperty("_id") val id: String,
    @JsonProperty("_version") val version: Int,
    val result: String? = null
)
