package org.taymyr.lagom.elasticsearch.search.dsl.query.compound

data class BoolQuery(val bool: BoolQueryBody) : CompoundQuery