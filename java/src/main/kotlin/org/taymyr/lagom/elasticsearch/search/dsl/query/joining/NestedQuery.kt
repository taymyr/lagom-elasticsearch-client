package org.taymyr.lagom.elasticsearch.search.dsl.query.joining

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-nested-query.html)
 */
data class NestedQuery(val nested: NestedQueryBody) : JoiningQuery