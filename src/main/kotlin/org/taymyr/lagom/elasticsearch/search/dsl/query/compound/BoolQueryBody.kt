package org.taymyr.lagom.elasticsearch.search.dsl.query.compound

import org.taymyr.lagom.elasticsearch.search.dsl.query.Query

data class BoolQueryBody(
    val should: List<Query>?,
    val mustNot: List<Query>?,
    val must: List<Query>?,
    val filter: List<Query>?
)