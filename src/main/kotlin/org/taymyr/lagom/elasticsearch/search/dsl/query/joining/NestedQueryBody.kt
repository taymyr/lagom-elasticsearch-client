package org.taymyr.lagom.elasticsearch.search.dsl.query.joining

import org.taymyr.lagom.elasticsearch.search.dsl.query.Query

data class NestedQueryBody(
    val path: String,
    val query: Query
) : JoiningQuery