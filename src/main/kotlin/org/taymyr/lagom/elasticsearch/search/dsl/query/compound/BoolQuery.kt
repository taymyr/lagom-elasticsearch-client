package org.taymyr.lagom.elasticsearch.search.dsl.query.compound

/**
 * @author Evgeny Stankevich {@literal <estankevich@fil-it.ru>}.
 */
data class BoolQuery(val bool: BoolQueryBody) : CompoundQuery