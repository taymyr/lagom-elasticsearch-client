package org.taymyr.lagom.elasticsearch.search.dsl.query.term

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-range-query.html#query-dsl-range-query)
 */
data class NumericRange(
    val gte: Int?,
    val gt: Int?,
    val lte: Int?,
    val lt: Int?,
    val boost: Double?
) : Range {

    class Builder {
        private val gte: Int? = null
        private val gt: Int? = null
        private val lte: Int? = null
        private val lt: Int? = null
        private val boost: Double? = null

        fun build() {
            if (listOfNotNull(gte, gt, lte, lt).isEmpty()) throw error("All field of NumericRange is null")
            NumericRange(gte, gt, lte, lt, boost)
        }
    }
}