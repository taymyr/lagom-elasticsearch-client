package org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext

import com.fasterxml.jackson.annotation.JsonProperty
import org.taymyr.lagom.elasticsearch.search.dsl.query.Query

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-query-phrase-prefix.html)
 */
data class MatchPhrasePrefixQuery(
    @JsonProperty("match_phrase_prefix")
    val matchPhrasePrefix: MatchPhrasePrefix
) : Query {
    companion object {
        @JvmStatic
        fun of(matchPhrasePrefix: MatchPhrasePrefix) = MatchPhrasePrefixQuery(matchPhrasePrefix)
    }
}