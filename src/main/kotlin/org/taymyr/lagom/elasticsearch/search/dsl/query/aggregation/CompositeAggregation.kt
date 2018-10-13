package org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation

data class CompositeAggregation(val composite: Composite) : Aggregation {

    data class Composite(val sources: List<Map<String, TermsAggregation>>) {
        companion object {
            @JvmStatic fun of(vararg srcs: Map<String, TermsAggregation>) = Composite(srcs.toList())
        }
    }
}