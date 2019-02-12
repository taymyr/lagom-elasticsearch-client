package org.taymyr.lagom.elasticsearch.indices.dsl

import com.fasterxml.jackson.annotation.JsonProperty

data class IndexInfo(val settings: Settings, val mappings: Map<String, Mapping>) {

    data class Settings(val index: Index)

    data class Index(
        @JsonProperty("number_of_shards") val numberOfShards: Int,
        @JsonProperty("number_of_replicas") val numberOfReplicas: Int
    )
}