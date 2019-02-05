package org.taymyr.lagom.elasticsearch.search.dsl.query.term

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-wildcard-query.html)
 */
data class WildcardQuery(val wildcard: Wildcard) : TermLevelQuery {
    companion object {
        @JvmStatic fun ofWildcard(wildcard: Wildcard) = WildcardQuery(wildcard)
    }
}