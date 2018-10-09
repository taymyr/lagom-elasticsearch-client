package org.taymyr.lagom.elasticsearch.search.dsl.query.term

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-terms-query.html)
 */
data class TermsQuery(val terms: List<Term>) : TermLevelQuery {
    companion object {
        @JvmStatic fun ofTerms(terms: List<Term>) = TermsQuery(terms)
        @JvmStatic fun ofTerms(vararg term: Term) = TermsQuery(term.asList())
    }
}