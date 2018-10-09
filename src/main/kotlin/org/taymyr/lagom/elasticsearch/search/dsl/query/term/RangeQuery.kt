package org.taymyr.lagom.elasticsearch.search.dsl.query.term

import java.util.OptionalInt

data class RangeQuery(val range: Range) : TermLevelQuery {
    interface Range
    companion object {
        @JvmStatic fun ofRange(range: Range) = RangeQuery(range)
    }
    data class LteGte(
        val lte: OptionalInt,
        val gte: OptionalInt
    )
}