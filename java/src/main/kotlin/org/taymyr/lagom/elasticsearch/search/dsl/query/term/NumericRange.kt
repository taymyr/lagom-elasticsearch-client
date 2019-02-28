package org.taymyr.lagom.elasticsearch.search.dsl.query.term

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-range-query.html#query-dsl-range-query)
 */
data class NumericRange(
    val gte: Int? = null,
    val gt: Int? = null,
    val lte: Int? = null,
    val lt: Int? = null,
    val boost: Double? = null
) : Range {

    class Builder {
        private var gte: Int? = null
        private var gt: Int? = null
        private var lte: Int? = null
        private var lt: Int? = null
        private var boost: Double? = null

        fun gte(gte: Int) = apply { this.gte = gte }
        fun gt(gt: Int) = apply { this.gt = gt }
        fun lte(lte: Int) = apply { this.lte = lte }
        fun lt(lt: Int) = apply { this.lt = lt }
        fun boost(boost: Double) = apply { this.boost = boost }

        fun build() =
            if (sequenceOf(gte, gt, lte, lt).any { it != null }) NumericRange(gte, gt, lte, lt, boost)
            else error("All field of NumericRange is null")
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()

        @JvmStatic
        fun gte(gte: Int) = NumericRange(gte = gte)

        @JvmStatic
        fun gt(gt: Int) = NumericRange(gt = gt)

        @JvmStatic
        fun lte(lte: Int) = NumericRange(lte = lte)

        @JvmStatic
        fun lt(lt: Int) = NumericRange(lt = lt)
    }
}