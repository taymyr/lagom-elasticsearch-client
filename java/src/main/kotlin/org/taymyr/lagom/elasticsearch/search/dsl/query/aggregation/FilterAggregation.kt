package org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation

import org.taymyr.lagom.elasticsearch.search.dsl.query.Query

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-filter-aggregation.html)
 */
data class FilterAggregation(
    val filter: Query,
    val aggs: Map<String, Aggregation>
) : Aggregation {
    companion object {
        @JvmStatic fun of(filter: Query, aggs: Map<String, Aggregation>) = FilterAggregation(filter, aggs)
        @JvmStatic fun of(filter: Query, vararg aggs: Pair<String, Aggregation>) = FilterAggregation(filter, aggs.toMap())
    }
}