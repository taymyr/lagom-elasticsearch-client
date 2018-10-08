package org.taymyr.lagom.elasticsearch.search.dsl.query.term

data class TermQuery(val term: Term) : TermLevelQuery {
    companion object {
        @JvmStatic fun of(term: Term) = TermQuery(term)
        @JvmStatic fun of(vararg term: Term) = term.map { of(it) }
    }
}