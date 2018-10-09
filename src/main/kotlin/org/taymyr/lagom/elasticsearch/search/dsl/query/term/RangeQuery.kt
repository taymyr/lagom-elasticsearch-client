package org.taymyr.lagom.elasticsearch.search.dsl.query.term

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-range-query.html)
 */
data class RangeQuery(val range: Range) : TermLevelQuery {
    companion object {
        @JvmStatic fun ofRange(range: Range) = RangeQuery(range)

        @JvmStatic fun numericRange() = NumericRange.Builder()
    }
}