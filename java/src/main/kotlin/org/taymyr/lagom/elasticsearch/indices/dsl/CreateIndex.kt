package org.taymyr.lagom.elasticsearch.indices.dsl

import com.fasterxml.jackson.annotation.JsonProperty

class CreateIndex @JvmOverloads constructor(
    val settings: Settings? = null,
    val mappings: Map<String, Mapping>? = null
) {

    data class Settings @JvmOverloads constructor(
        @JsonProperty("number_of_shards") val numberOfShards: Int?,
        @JsonProperty("number_of_replicas") val numberOfReplicas: Int?,
        val analysis: Analysis? = null
    )

    data class Analysis @JvmOverloads constructor(
        val filter: Map<String, Filter>? = null,
        val analyzer: Map<String, Analyzer>? = null
    )
}
