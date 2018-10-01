package org.taymyr.lagom.elasticsearch.test

import com.fasterxml.jackson.annotation.JsonProperty
import org.taymyr.lagom.elasticsearch.dsl.settings.Filter

data class AutocompleteFilter(
    override val type: String,
    @get:JsonProperty("min_gram")
    val minGram: Int,
    @get:JsonProperty("max_gram")
    val maxGram: Int
) : Filter(type)
