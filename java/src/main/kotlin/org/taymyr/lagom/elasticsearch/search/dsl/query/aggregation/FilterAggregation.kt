package org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation

import org.taymyr.lagom.elasticsearch.search.dsl.query.Query

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-filter-aggregation.html)
 */
data class FilterAggregation(
    val filter: Query,
    val aggs: Map<String, Aggregation>
) : Aggregation {

    class Builder {
        private var filter: Query? = null
        private var aggs: MutableMap<String, Aggregation> = mutableMapOf()

        fun filter(filter: Query) = apply { this.filter = filter }
        fun agg(name: String, aggregation: Aggregation) = apply { this.aggs[name] = aggregation }

        fun build() = FilterAggregation(
            filter = filter ?: error("Field 'filter' can't be null"),
            aggs = if (aggs.isEmpty()) error("Field 'aggs' can't be empty") else aggs.toMap()
        )
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }
}