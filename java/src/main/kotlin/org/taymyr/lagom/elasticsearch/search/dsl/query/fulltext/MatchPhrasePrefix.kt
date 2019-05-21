package org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext

import com.fasterxml.jackson.annotation.JsonProperty

data class MatchPhrasePrefix @JvmOverloads constructor(
    val query: String,
    @JsonProperty("max_expansions")
    val maxExpansions: Int? = null
)