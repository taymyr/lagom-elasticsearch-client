package org.taymyr.lagom.elasticsearch

import org.taymyr.lagom.elasticsearch.indices.dsl.Filter

data class AutocompleteFilter(
    override val type: String,
    val minGram: Int,
    val maxGram: Int
) : Filter(type)