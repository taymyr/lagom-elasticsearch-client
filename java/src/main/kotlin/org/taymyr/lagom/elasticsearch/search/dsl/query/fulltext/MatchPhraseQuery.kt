package org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.taymyr.lagom.elasticsearch.search.dsl.query.Query

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-query-phrase.html)
 */
@JsonSerialize(using = MatchPhraseQuery.Serializer::class)
data class MatchPhraseQuery(
    @JsonIgnore
    val field: String,
    @JsonProperty("match_phrase")
    val matchPhrase: MatchPhrase

) : Query {

    class Serializer : JsonSerializer<MatchPhraseQuery>() {
        override fun serialize(value: MatchPhraseQuery?, gen: JsonGenerator?, serializers: SerializerProvider?) {
            gen?.run {
                value?.let { query ->
                    writeStartObject()
                    writeFieldName("match_phrase")
                    writeStartObject()
                    writeObjectField(query.field, query.matchPhrase)
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

        fun field(field: String) = apply { this.field = field }
        fun query(query: String) = apply { this.query = query }
        fun analyzer(analyzer: String) = apply { this.analyzer = analyzer }
        fun zeroTermsQuery(zeroTermsQuery: ZeroTerms) = apply { this.zeroTermsQuery = zeroTermsQuery }
        fun slop(slop: Int) = apply { this.slop = slop }

        fun build() = MatchPhraseQuery(
            field ?: error("Field name can't be null"),
            MatchPhrase(
                query = query ?: error("Query can't be null"),
                analyzer = analyzer,
                zeroTermsQuery = zeroTermsQuery,
                slop = slop
            )
        )
    }

    companion object {
        @JvmStatic
        fun builder() = MatchPhraseQuery.Builder()

        @JvmStatic
        fun of(field: String, query: String) = MatchPhraseQuery(field, MatchPhrase(query))
    }
}