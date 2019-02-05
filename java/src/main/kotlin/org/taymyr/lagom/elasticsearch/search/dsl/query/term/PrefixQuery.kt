package org.taymyr.lagom.elasticsearch.search.dsl.query.term

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-prefix-query.html)
 */
class PrefixQuery(val prefix: Prefix) : TermLevelQuery {
    companion object {
        @JvmStatic fun ofPrefix(prefix: Prefix) = PrefixQuery(prefix)
    }
}