package org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation

import java.math.BigDecimal

data class StatsAggregation(
    val stats: StatsFieldSpec,
    val missing: BigDecimal? = null
) : Aggregation {
    data class StatsFieldSpec(val field: String)

    companion object {
        @JvmStatic
        @JvmOverloads
        fun statsAggregation(field: String, missing: BigDecimal? = null) = StatsAggregation(StatsFieldSpec(field), missing)
    }
}

data class StatsAggregationResult(
    val count: Int,
    val min: BigDecimal?,
    val max: BigDecimal?,
    val avg: BigDecimal?,
    val sum: BigDecimal?
)
