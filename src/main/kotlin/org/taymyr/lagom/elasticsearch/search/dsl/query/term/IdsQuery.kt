package org.taymyr.lagom.elasticsearch.search.dsl.query.term

import org.taymyr.lagom.elasticsearch.search.dsl.query.Ids

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-ids-query.html).
 */
data class IdsQuery(val ids: Ids) : TermLevelQuery