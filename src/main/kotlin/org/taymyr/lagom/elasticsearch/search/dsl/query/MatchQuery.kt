package org.taymyr.lagom.elasticsearch.search.dsl.query

/**
 * @author Ilya Korshunov
 */
data class MatchQuery(val match: Match) : Query {
    interface Match
    companion object {
        @JvmStatic fun of(match: Match) = MatchQuery(match)
    }
}