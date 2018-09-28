package org.taymyr.lagom.elasticsearch.client.dsl

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.taymyr.lagom.elasticsearch.client.DTOAnnotation

@DTOAnnotation
data class BoolQuery @JsonCreator constructor(
        @get:JsonInclude(JsonInclude.Include.NON_EMPTY)
        val should: List<Query>?,

        @get:JsonInclude(JsonInclude.Include.NON_EMPTY)
        @JsonProperty("must_not")
        val mustNot: List<Query>?,

        @get:JsonInclude(JsonInclude.Include.NON_EMPTY)
        val must: List<Query>?
) : Query {

    class Builder {

        private var should: List<Query>? = null
        private var mustNot: List<Query>? = null
        private var must: List<Query>? = null

        fun should(should: List<Query>?) = apply { this.should = should }
        fun should(vararg should: Query) = should(should.asList())
        fun mustNot(mustNot: List<Query>?) = apply { this.mustNot = mustNot }
        fun mustNot(vararg mustNot: Query) = mustNot(mustNot.asList())
        fun must(must: List<Query>?) = apply { this.must = must }
        fun must(vararg  must: Query) = must(must.asList())

        fun build() = BoolQuery(should, mustNot, must)

    }

}
