package org.taymyr.lagom.elasticsearch.search.dsl.query.term

data class TermsQuery(val terms: List<Term>) : TermLevelQuery {
    companion object {
        @JvmStatic fun of(vararg term: Term) = TermsQuery(term.asList())
    }
}