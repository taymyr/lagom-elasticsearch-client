package org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext

import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MultiMatchQueryType.BEST_FIELDS

data class MultiMatch @JvmOverloads constructor(
    val query: String,
    val fields: List<String>,
    val type: MultiMatchQueryType = BEST_FIELDS
)