package org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.taymyr.lagom.elasticsearch.search.dsl.query.Query

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-query.html)
 */
@JsonSerialize(using = MatchQuery.Serializer::class)
data class MatchQuery(@JsonIgnore val field: String, val match: Match) : Query {

    class Serializer : JsonSerializer<MatchQuery>() {
        override fun serialize(value: MatchQuery?, gen: JsonGenerator?, serializers: SerializerProvider?) {
            gen?.run {
                value?.let { query ->
                    writeStartObject()
                    writeFieldName("match")
                    writeStartObject()
                    writeObjectField(query.field, query.match)
                    writeEndObject()
                    writeEndObject()
                }
            }
        }
    }

    class Builder {
        private var field: String? = null
        private var query: String? = null
        private var operator: MatchOperator? = null
        private var minimumShouldMatch: String? = null
        private var analyzer: String? = null
        private var lenient: Boolean? = null
        private var zeroTermsQuery: ZeroTerms? = null
        private var cutoffFrequency: Double? = null
        private var autoGenerateSynonymsPhraseQuery: Boolean? = null

        fun field(field: String) = apply { this.field = field }
        fun query(query: String) = apply { this.query = query }
        fun operator(operator: MatchOperator) = apply { this.operator = operator }
        fun minimumShouldMatch(minimumShouldMatch: String) = apply { this.minimumShouldMatch = minimumShouldMatch }
        fun analyzer(analyzer: String) = apply { this.analyzer = analyzer }
        fun lenient(lenient: Boolean) = apply { this.lenient = lenient }
        fun zeroTermsQuery(zeroTermsQuery: ZeroTerms) = apply { this.zeroTermsQuery = zeroTermsQuery }
        fun cutoffFrequency(cutoffFrequency: Double) = apply { this.cutoffFrequency = cutoffFrequency }
        fun autoGenerateSynonymsPhraseQuery(autoGenerateSynonymsPhraseQuery: Boolean) =
            apply { this.autoGenerateSynonymsPhraseQuery = autoGenerateSynonymsPhraseQuery }

        fun build() = MatchQuery(
            field ?: error("Field name can't be null"),
            Match(
                query = query ?: error("Query can't be null"),
                operator = operator,
                minimumShouldMatch = minimumShouldMatch,
                analyzer = analyzer,
                lenient = lenient,
                zeroTermsQuery = zeroTermsQuery,
                cutoffFrequency = cutoffFrequency,
                autoGenerateSynonymsPhraseQuery = autoGenerateSynonymsPhraseQuery
            )
        )
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()

        @JvmStatic
        fun of(field: String, query: String) = MatchQuery(field, Match(query))
    }
}
