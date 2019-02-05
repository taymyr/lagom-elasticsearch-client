package org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-nested-aggregation.html)
 */
data class NestedAggregation(
    val nested: NestedPath,
    val aggs: Map<String, Aggregation>
) : Aggregation {
    data class NestedPath(val path: String)
    companion object {
        @JvmStatic fun of(path: String, aggs: Map<String, Aggregation>) = NestedAggregation(NestedPath(path), aggs)
        @JvmStatic fun of(path: String, vararg aggs: Pair<String, Aggregation>) = NestedAggregation(NestedPath(path), aggs.toMap())
    }
}