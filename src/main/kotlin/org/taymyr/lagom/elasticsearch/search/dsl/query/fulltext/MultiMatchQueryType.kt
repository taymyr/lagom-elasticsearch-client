package org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-multi-match-query.html#query-dsl-multi-match-query)
 */
enum class MultiMatchQueryType(val title: String) {
    BEST_FIELDS("best_fields"),
    MOST_FIELDS("most_fields"),
    CROSS_FIELDS("cross_fields"),
    PHRASE("phrase"),
    PHRASE_PREFIX("phrase_prefix")
}