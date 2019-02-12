package org.taymyr.lagom.elasticsearch.search.dsl.query.term

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-wildcard-query.html)
 */
@JsonSerialize(using = WildcardQuery.WildcardQuerySerializer::class)
data class WildcardQuery(val field: String, val wildcard: Wildcard) : TermLevelQuery {

    class WildcardQuerySerializer : JsonSerializer<WildcardQuery>() {
        override fun serialize(value: WildcardQuery?, gen: JsonGenerator?, serializers: SerializerProvider?) {
            gen?.run {
                value?.let { wildcardQuery ->
                    writeStartObject()
                    writeFieldName("wildcard")
                    writeStartObject()
                    writeObjectField(wildcardQuery.field, wildcardQuery.wildcard)
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

        fun build() = WildcardQuery(
            field = field ?: error("Field name can't be null"),
            wildcard = Wildcard(
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
        fun of(field: String, value: String, boost: Double? = null) = WildcardQuery(field, Wildcard(value, boost))
    }
}