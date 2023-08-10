package org.taymyr.lagom.elasticsearch.search.dsl.query.term

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-term-query.html)
 */
@JsonSerialize(using = TermQuery.TermQuerySerializer::class)
data class TermQuery(val field: String, val term: Term) : TermLevelQuery {

    class TermQuerySerializer : JsonSerializer<TermQuery>() {
        override fun serialize(value: TermQuery?, gen: JsonGenerator?, serializers: SerializerProvider?) {
            gen?.run {
                value?.let { termQuery ->
                    writeStartObject()
                    writeFieldName("term")
                    writeStartObject()
                    writeObjectField(termQuery.field, termQuery.term)
                    writeEndObject()
                    writeEndObject()
                }
            }
        }
    }

    class Builder {
        private var field: String? = null
        private var value: String? = null
        private var boost: Double? = null

        fun field(field: String) = apply { this.field = field }
        fun value(value: String) = apply { this.value = value }
        fun boost(boost: Double) = apply { this.boost = boost }

        fun build() = TermQuery(
            field = field ?: error("Field name can't be null"),
            term = Term(
                value = value ?: error("Value can't be null"),
                boost = boost
            )
        )
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()

        @JvmStatic
        @JvmOverloads
        fun of(field: String, value: String, boost: Double? = null) = TermQuery(field, Term(value, boost))
    }
}
