package org.taymyr.lagom.elasticsearch.dsl.settings

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import org.taymyr.lagom.elasticsearch.DTOAnnotation

@DTOAnnotation
@JsonRootName("settings")
data class ElasticSearchIndexSettings(
    @get:JsonProperty("number_of_shards")
    val numberOfShards: Int,
    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    val analysis: Analysis?
) {

    data class Analysis(
        @get:JsonInclude(JsonInclude.Include.NON_EMPTY)
        val filter: Map<String, Filter>?,
        @get:JsonInclude(JsonInclude.Include.NON_EMPTY)
        val analyzer: Map<String, Analyzer>?
    )
}
