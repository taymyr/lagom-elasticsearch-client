package org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext

import com.fasterxml.jackson.annotation.JsonProperty

data class Match @JvmOverloads constructor(
    val query: String,
    val operator: MatchOperator? = null,
    @JsonProperty("minimum_should_match")
    val minimumShouldMatch: String? = null,
    val analyzer: String? = null,
    val lenient: Boolean? = null,
    @JsonProperty("zero_terms_query")
    val zeroTermsQuery: ZeroTerms? = null,
    @JsonProperty("cutoff_frequency")
    val cutoffFrequency: Double? = null,
    @JsonProperty("auto_generate_synonyms_phrase_query")
    val autoGenerateSynonymsPhraseQuery: Boolean? = null
)