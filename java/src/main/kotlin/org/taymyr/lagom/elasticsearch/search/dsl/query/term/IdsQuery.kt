package org.taymyr.lagom.elasticsearch.search.dsl.query.term

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-ids-query.html).
 */
data class IdsQuery(val ids: Ids) : TermLevelQuery