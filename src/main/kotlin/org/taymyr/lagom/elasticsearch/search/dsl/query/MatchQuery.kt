package org.taymyr.lagom.elasticsearch.search.dsl.query

data class MatchQuery(val match: Match) : Query {
    interface Match
    companion object {
        @JvmStatic fun of(match: Match) = MatchQuery(match)
    }
}