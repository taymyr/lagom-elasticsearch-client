package org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation

import java.math.BigDecimal

data class RangeAggregation(
    val range: RangeAggregationSpec,
    val aggs: Map<String, Aggregation>? = null
) : Aggregation {

    class Builder {
        private var field = ""
        private var keyed = false
        private val ranges = mutableListOf<RangeSpec>()
        private var stats = false
        private val aggs = mutableMapOf<String, Aggregation>()

        fun field(value: String): Builder = apply { field = value }
        fun keyed(value: Boolean): Builder = apply { keyed = value }
        fun range(from: BigDecimal? = null, to: BigDecimal? = null): Builder = apply {
            ranges += RangeSpec(from, to)
        }
        fun range(from: Int? = null, to: Int? = null): Builder = apply {
            ranges += RangeSpec(from?.toBigDecimal(), to?.toBigDecimal())
        }
        fun stats(): Builder = apply { stats = true }
        fun agg(name: String, aggregation: Aggregation): Builder = apply { aggs[name] = aggregation }

        fun build(): RangeAggregation {
            if (field.isBlank()) error("'field' should not be blank")
            if (stats) {
                aggs[DEFAULT_STATS_AGG_NAME] = StatsAggregation(StatsAggregation.StatsFieldSpec(field))
            }
            return RangeAggregation(
                RangeAggregationSpec(
                    field,
                    keyed,
                    if (ranges.isEmpty()) error("'ranges' should not be empty") else ranges
                ),
                if (aggs.isEmpty()) null else aggs.toMap()
            )
        }
    }

    companion object {
        const val DEFAULT_STATS_AGG_NAME = "agg_stats"

        @JvmStatic
        fun builder(): Builder = Builder()
    }
}

data class RangeAggregationSpec(
    val field: String,
    val keyed: Boolean = false,
    val ranges: List<RangeSpec>
)

data class RangeSpec(
    val from: BigDecimal?,
    val to: BigDecimal?
)
