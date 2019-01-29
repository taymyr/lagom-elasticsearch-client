package org.taymyr.lagom.elasticsearch.indices.dsl

import com.fasterxml.jackson.annotation.JsonProperty
import javax.annotation.concurrent.Immutable

@Immutable
class CreateIndex(
    val settings: Settings? = null,
    val mappings: Map<String, Mapping>? = null
) {

    @Immutable
    data class Settings(
        @JsonProperty("number_of_shards") val numberOfShards: Int?,
        @JsonProperty("number_of_replicas") val numberOfReplicas: Int?,
        val analysis: Analysis? = null
    )

    @Immutable
    data class Analysis(
        val filter: Map<String, Filter>?,
        val analyzer: Map<String, Analyzer>?
    )
}
