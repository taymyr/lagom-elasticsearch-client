package org.taymyr.lagom.elasticsearch.search.dsl

import com.fasterxml.jackson.annotation.JsonProperty

data class HitResult<T>(
    @JsonProperty("_index") val index: String,
    @JsonProperty("_type") val type: String,
    @JsonProperty("_id") val id: String,
    @JsonProperty("_score") val score: Double,
    @JsonProperty("_source") val source: T,
    @JsonProperty("sort") val sort: List<Any>?,
    @JsonProperty("highlight") val highlight: Map<String, List<String>>?
)
