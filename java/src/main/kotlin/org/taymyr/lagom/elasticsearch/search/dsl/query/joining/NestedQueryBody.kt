package org.taymyr.lagom.elasticsearch.search.dsl.query.joining

import com.fasterxml.jackson.annotation.JsonProperty
import org.taymyr.lagom.elasticsearch.search.dsl.query.Query

data class NestedQueryBody @JvmOverloads constructor(
    val path: String,
    val query: Query,
    @JsonProperty("score_mode")
    val scoreMode: NestedQueryScoreMode? = null
) : JoiningQuery