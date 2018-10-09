package org.taymyr.lagom.elasticsearch.search.dsl.query.term

data class TermQuery(val term: Term) : TermLevelQuery {
    companion object {
        @JvmStatic fun ofTerm(term: Term) = TermQuery(term)
        @JvmStatic fun ofTerm(vararg term: Term) = term.map { ofTerm(it) }
    }
}