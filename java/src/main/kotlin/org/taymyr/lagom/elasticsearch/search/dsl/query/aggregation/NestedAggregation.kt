package org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-nested-aggregation.html)
 */
data class NestedAggregation(
    val nested: NestedPath,
    val aggs: Map<String, Aggregation>
) : Aggregation {

    data class NestedPath(val path: String)

    class Builder {
        private var nested: String? = null
        private var aggs: MutableMap<String, Aggregation> = mutableMapOf()

        fun nested(nested: String) = apply { this.nested = nested }
        fun agg(name: String, aggregation: Aggregation) = apply { this.aggs[name] = aggregation }

        fun build() = NestedAggregation(
            nested = NestedPath(nested ?: error("Field 'nested' can't be null")),
            aggs = if (aggs.isEmpty()) error("Field 'aggs' can't be empty") else aggs.toMap()
        )
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }
}
