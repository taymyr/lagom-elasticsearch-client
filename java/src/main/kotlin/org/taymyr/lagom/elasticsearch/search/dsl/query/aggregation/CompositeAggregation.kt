package org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-composite-aggregation.html)
 */
data class CompositeAggregation(val composite: Composite) : Aggregation {

    data class Composite(val sources: List<Map<String, TermsAggregation>>)

    class SourceBuilder {
        private var aggs: MutableMap<String, TermsAggregation> = mutableMapOf()

        fun agg(name: String, aggregation: TermsAggregation) = apply { this.aggs[name] = aggregation }

        fun build() = if (aggs.isEmpty()) error("Field 'aggs' can't be empty") else aggs.toMap()
    }

    class Builder {
        private var sources: MutableList<SourceBuilder> = mutableListOf()

        fun source(source: SourceBuilder) = apply { this.sources.add(source) }

        fun build() = CompositeAggregation(
            Composite(if (sources.isEmpty()) error("CompositeAggregation can't be empty") else sources.map { it.build() })
        )
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()

        @JvmStatic
        fun sourceBuilder() = SourceBuilder()
    }
}