package org.taymyr.lagom.elasticsearch.search.dsl.query.term

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-term-query.html)
 */
data class TermQuery(val term: Term) : TermLevelQuery {
    companion object {
        @JvmStatic fun ofTerm(term: Term) = TermQuery(term)
    }
}