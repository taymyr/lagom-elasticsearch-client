package org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation

data class NestedAggregation(
    val nested: NestedPath,
    val aggs: Map<String, Aggregation>
) : Aggregation {
    data class NestedPath(val path: String)
    companion object {
        fun of(path: String, aggs: Map<String, Aggregation>) = NestedAggregation(NestedPath(path), aggs)
        fun of(path: String, vararg aggs: Pair<String, Aggregation>) = NestedAggregation(NestedPath(path), aggs.toMap())
    }
}