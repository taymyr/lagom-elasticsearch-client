package org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext

import org.taymyr.lagom.elasticsearch.search.dsl.query.Query

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-query.html)
 */
data class MatchQuery(val match: Match) : Query {
    companion object {
        @JvmStatic fun ofMatch(match: Match) = MatchQuery(match)
    }
}