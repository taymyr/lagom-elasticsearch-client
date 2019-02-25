package org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation

import org.taymyr.lagom.elasticsearch.search.dsl.query.Order

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html)
 */
data class TermsAggregation @JvmOverloads constructor(
    val terms: FieldSpec,
    val aggs: Map<String, Aggregation>? = null
) : Aggregation {

    data class FieldSpec @JvmOverloads constructor(
        val field: String,
        val size: Int? = null,
        val order: Order? = null
    )

    class Builder {
        private var field: String? = null
        private var size: Int? = null
        private var order: Order? = null
        private var aggs: MutableMap<String, Aggregation> = mutableMapOf()

        fun field(field: String) = apply { this.field = field }
        fun size(size: Int) = apply { this.size = size }
        fun order(order: Order) = apply { this.order = order }
        fun agg(name: String, aggregation: Aggregation) = apply { this.aggs[name] = aggregation }

        fun build() = TermsAggregation(
            terms = FieldSpec(
                field = field ?: error("Field 'field' can't be null"),
                size = size,
                order = order
            ),
            aggs = if (aggs.isEmpty()) null else aggs.toMap()
        )
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }
}