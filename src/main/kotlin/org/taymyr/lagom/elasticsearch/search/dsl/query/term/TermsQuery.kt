package org.taymyr.lagom.elasticsearch.search.dsl.query.term

data class TermsQuery(val terms: List<Term>) : TermLevelQuery {
    companion object {
        @JvmStatic fun ofTerms(vararg term: Term) = TermsQuery(term.asList())
    }
}