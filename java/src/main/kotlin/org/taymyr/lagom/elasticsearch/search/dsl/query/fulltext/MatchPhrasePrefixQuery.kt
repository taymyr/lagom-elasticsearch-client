package org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.taymyr.lagom.elasticsearch.search.dsl.query.Query

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-query-phrase-prefix.html)
 */
@JsonSerialize(using = MatchPhrasePrefixQuery.Serializer::class)
data class MatchPhrasePrefixQuery(
    @JsonIgnore
    val field: String,
    @JsonProperty("match_phrase_prefix")
    val matchPhrasePrefix: MatchPhrasePrefix
) : Query {

    class Serializer : JsonSerializer<MatchPhrasePrefixQuery>() {
        override fun serialize(value: MatchPhrasePrefixQuery?, gen: JsonGenerator?, serializers: SerializerProvider?) {
            gen?.run {
                value?.let { query ->
                    writeStartObject()
                    writeFieldName("match_phrase_prefix")
                    writeStartObject()
                    writeObjectField(query.field, query.matchPhrasePrefix)
                    writeEndObject()
                    writeEndObject()
                }
            }
        }
    }

    class Builder {
        private var field: String? = null
        private var query: String? = null
        private var analyzer: String? = null
        private var zeroTermsQuery: ZeroTerms? = null
        private var slop: Int? = null
        private var maxExpansions: Int? = null

        fun field(field: String) = apply { this.field = field }
        fun query(query: String) = apply { this.query = query }
        fun analyzer(analyzer: String) = apply { this.analyzer = analyzer }
        fun zeroTermsQuery(zeroTermsQuery: ZeroTerms) = apply { this.zeroTermsQuery = zeroTermsQuery }
        fun slop(slop: Int) = apply { this.slop = slop }
        fun maxExpansions(maxExpansions: Int) = apply { this.maxExpansions = maxExpansions }

        fun build() = MatchPhrasePrefixQuery(
            field ?: error("Field name can't be null"),
            MatchPhrasePrefix(
                query = query ?: error("Query can't be null"),
                analyzer = analyzer,
                zeroTermsQuery = zeroTermsQuery,
                slop = slop,
                maxExpansions = maxExpansions
            )
        )
    }

    companion object {
        @JvmStatic
        fun builder() = MatchPhrasePrefixQuery.Builder()

        @JvmStatic
        fun of(field: String, query: String) = MatchPhrasePrefixQuery(field, MatchPhrasePrefix(query))
    }
}
