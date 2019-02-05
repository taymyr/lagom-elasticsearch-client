package org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-composite-aggregation.html)
 */
data class CompositeAggregation(val composite: Composite) : Aggregation {

    data class Composite(val sources: List<Map<String, TermsAggregation>>) {
        companion object {
            @JvmStatic fun of(vararg srcs: Map<String, TermsAggregation>) = Composite(srcs.toList())
        }
    }
}