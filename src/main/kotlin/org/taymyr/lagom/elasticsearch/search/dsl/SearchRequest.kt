package org.taymyr.lagom.elasticsearch.search.dsl

import org.taymyr.lagom.elasticsearch.search.dsl.query.Query
import org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation.Aggregation

data class SearchRequest(
    val query: Query,
    val from: Int? = null,
    val size: Int? = null,
    val aggs: Map<String, Aggregation>? = null
)