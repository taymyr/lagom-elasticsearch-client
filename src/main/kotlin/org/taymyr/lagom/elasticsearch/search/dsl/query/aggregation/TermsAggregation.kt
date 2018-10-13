package org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation

import org.taymyr.lagom.elasticsearch.search.dsl.query.Order

data class TermsAggregation(
    val terms: FieldSpec,
    val aggs: Map<String, Aggregation>? = null
) : Aggregation {
    data class FieldSpec(
        val field: String,
        val size: Int? = null,
        val order: Order? = null
    )
    companion object {
        @JvmStatic fun of(terms: FieldSpec, aggs: Map<String, Aggregation>) = TermsAggregation(terms, aggs)
        @JvmStatic fun of(terms: FieldSpec, vararg aggs: Pair<String, Aggregation>) = TermsAggregation(terms, aggs.toMap())
        @JvmStatic fun of(terms: FieldSpec) = TermsAggregation(terms)
    }
}