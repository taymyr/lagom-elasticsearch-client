package org.taymyr.lagom.elasticsearch.search.dsl

import com.fasterxml.jackson.annotation.JsonProperty

data class SuggestResult<T>(
    val text: String,
    val options: List<SuggestOption<T>>
) {

    data class SuggestOption<T> @JvmOverloads constructor(
        val text: String,
        @JsonProperty("_source")
        val source: T? = null
    )
}