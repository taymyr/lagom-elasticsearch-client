package org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext

import com.fasterxml.jackson.annotation.JsonProperty
import org.taymyr.lagom.elasticsearch.search.dsl.query.Query

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-query-phrase.html)
 */
data class MatchPhraseQuery(
    @JsonProperty("match_phrase")
    val matchPhrase: MatchPhrase
) : Query {
    companion object {
        @JvmStatic fun of(matchPhrase: MatchPhrase) = MatchPhraseQuery(matchPhrase)
    }
}