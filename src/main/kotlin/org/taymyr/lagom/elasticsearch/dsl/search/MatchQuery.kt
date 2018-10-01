package org.taymyr.lagom.elasticsearch.dsl.search

import org.taymyr.lagom.elasticsearch.DTOAnnotation

@DTOAnnotation
data class MatchQuery(
    val match: Match
) : Query {

    interface Match

    companion object {

        @JvmStatic fun of(match: Match) = MatchQuery(match)
    }
}
