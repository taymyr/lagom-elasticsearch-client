package org.taymyr.lagom.elasticsearch.search.dsl.query.joining

data class NestedQuery(val nested: NestedQueryBody) : JoiningQuery