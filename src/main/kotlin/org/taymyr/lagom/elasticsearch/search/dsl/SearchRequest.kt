package org.taymyr.lagom.elasticsearch.search.dsl

import com.fasterxml.jackson.annotation.JsonProperty
import org.taymyr.lagom.elasticsearch.search.dsl.query.Query
import org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation.Aggregation

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-body.html)
 *
 * @param aggs See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations.html)
 */
data class SearchRequest @JvmOverloads constructor(
    val query: Query,
    val from: Int? = null,
    val size: Int? = null,
    val aggs: Map<String, Aggregation>? = null,
    @JsonProperty("post_filter")
    val postFilter: Query? = null
)