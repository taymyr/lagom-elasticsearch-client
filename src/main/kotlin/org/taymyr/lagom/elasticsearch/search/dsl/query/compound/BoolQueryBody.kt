package org.taymyr.lagom.elasticsearch.search.dsl.query.compound

import com.fasterxml.jackson.annotation.JsonProperty
import javax.management.Query

data class BoolQueryBody(
    val should: List<Query>?,
    @get:JsonProperty("must_not")
    val mustNot: List<Query>?,
    val must: List<Query>?,
    val filter: List<Query>?
) {
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

        fun build() = BoolQueryBody(should, mustNot, must, filter)
    }
}