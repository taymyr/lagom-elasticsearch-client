package org.taymyr.lagom.elasticsearch.search.dsl.query.compound

import org.taymyr.lagom.elasticsearch.search.dsl.query.Query

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-bool-query.html)
 */
data class BoolQuery(val bool: BoolQueryBody) : CompoundQuery {

    class Builder {
        private var should: List<Query>? = null
        private var mustNot: List<Query>? = null
        private var must: List<Query>? = null
        private var filter: List<Query>? = null

        fun should(should: List<Query>?) = apply { this.should = should }
        fun should(vararg should: Query) = should(should.asList())

        fun mustNot(mustNot: List<Query>?) = apply { this.mustNot = mustNot }
        fun mustNot(vararg mustNot: Query) = mustNot(mustNot.asList())

        fun must(must: List<Query>?) = apply { this.must = must }
        fun must(vararg must: Query) = must(must.asList())

        fun filter(filter: List<Query>?) = apply { this.filter = filter }
        fun filter(vararg filter: Query) = filter(filter.asList())

        fun build() = BoolQuery(BoolQueryBody(should, mustNot, must, filter))
    }

    companion object {
        @JvmStatic fun boolQuery() = Builder()
    }
}