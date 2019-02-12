package org.taymyr.lagom.elasticsearch.search.dsl.query.term

import com.fasterxml.jackson.annotation.JsonProperty

data class Regexp @JvmOverloads constructor(
    val value: String,
    val boost: Double? = null,
    val flags: String? = null,
    @JsonProperty("max_determinized_states")
    val maxDeterminizedStates: Int? = null
)