package org.taymyr.lagom.elasticsearch.indices.dsl

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateIndexResult(
    @JsonProperty("shards_acknowledged") val shardsAcknowledged: Boolean,
    val acknowledged: Boolean,
    val index: String
)
