package org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation

data class GlobalAggregation(
    val aggs: Map<String, Aggregation>
) : Aggregation {
    val global = mapOf<String, Any>() // Need to produce the global aggregation marker field "global": {}

    class Builder {
        private val aggs = mutableMapOf<String, Aggregation>()

        fun agg(name: String, aggregation: Aggregation): Builder = apply { aggs[name] = aggregation }
        fun build(): GlobalAggregation = GlobalAggregation(aggs)
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
