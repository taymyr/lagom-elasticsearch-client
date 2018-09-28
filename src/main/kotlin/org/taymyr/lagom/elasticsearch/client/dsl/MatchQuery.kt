package org.taymyr.lagom.elasticsearch.client.dsl

import org.taymyr.lagom.elasticsearch.client.DTOAnnotation

@DTOAnnotation
data class MatchQuery(
        val match: Match
) : Query {

    interface Match

    companion object {

        @JvmStatic fun of(match: Match) = MatchQuery(match)

    }

}
