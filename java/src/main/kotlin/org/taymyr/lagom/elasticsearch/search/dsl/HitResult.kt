package org.taymyr.lagom.elasticsearch.search.dsl

import com.fasterxml.jackson.annotation.JsonProperty

data class HitResult<T>(
    @JsonProperty("_score") val score: Double,
    @JsonProperty("_source") val source: T,
    @JsonProperty("sort") val sort: List<Any>?
)