package org.taymyr.lagom.elasticsearch.search.dsl.query.term

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-regexp-query.html)
 */
data class RegexpQuery(val regexp: Regexp) : TermLevelQuery {
    companion object {
        @JvmStatic fun ofRegexp(regexp: Regexp) = RegexpQuery(regexp)
    }
}