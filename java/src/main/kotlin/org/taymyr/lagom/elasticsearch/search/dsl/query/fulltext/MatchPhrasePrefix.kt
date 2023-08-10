package org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext

import com.fasterxml.jackson.annotation.JsonProperty

data class MatchPhrasePrefix @JvmOverloads constructor(
    val query: String,
    val analyzer: String? = null,
    @JsonProperty("zero_terms_query")
    val zeroTermsQuery: ZeroTerms? = null,
    val slop: Int? = null,
    @JsonProperty("max_expansions")
    val maxExpansions: Int? = null
)
