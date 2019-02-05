package org.taymyr.lagom.elasticsearch.search.dsl.query.term

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-terms-query.html)
 */
data class TermsQuery(val terms: Map<String, List<String>>) : TermLevelQuery {
    companion object {
        @JvmStatic fun ofTerms(terms: Map<String, List<String>>) = TermsQuery(terms)
        @JvmStatic fun ofTerms(vararg terms: Pair<String, List<String>>) = TermsQuery(terms.toMap())
    }
}