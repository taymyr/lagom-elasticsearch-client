package org.taymyr.lagom.elasticsearch.search.dsl

import org.taymyr.lagom.elasticsearch.search.dsl.query.Query

data class SearchRequest(
    val query: Query,
    val from: Int? = null,
    val size: Int? = null
)