package org.taymyr.lagom.elasticsearch

import com.fasterxml.jackson.annotation.JsonProperty
import org.taymyr.lagom.elasticsearch.indices.dsl.Filter

/**
 * @author Ilya Korshunov
 */
data class AutocompleteFilter(
    override val type: String,
    @get:JsonProperty("min_gram")
    val minGram: Int,
    @get:JsonProperty("max_gram")
    val maxGram: Int
) : Filter(type)