package org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext

import com.fasterxml.jackson.annotation.JsonValue

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-multi-match-query.html#query-dsl-multi-match-query)
 */
enum class MultiMatchQueryType(@JsonValue val title: String) {
    BEST_FIELDS("best_fields"),
    CROSS_FIELDS("cross_fields"),
    MOST_FIELDS("most_fields"),
    PHRASE("phrase"),
    PHRASE_PREFIX("phrase_prefix")
}