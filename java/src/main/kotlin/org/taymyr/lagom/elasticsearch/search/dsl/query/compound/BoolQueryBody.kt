package org.taymyr.lagom.elasticsearch.search.dsl.query.compound

import com.fasterxml.jackson.annotation.JsonProperty
import org.taymyr.lagom.elasticsearch.search.dsl.query.Query

data class BoolQueryBody(
    val should: List<Query>?,
    @JsonProperty("must_not")
    val mustNot: List<Query>?,
    val must: List<Query>?,
    val filter: List<Query>?
)
