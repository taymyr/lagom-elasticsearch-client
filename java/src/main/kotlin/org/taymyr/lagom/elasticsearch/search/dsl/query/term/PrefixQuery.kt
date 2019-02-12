package org.taymyr.lagom.elasticsearch.search.dsl.query.term

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-prefix-query.html)
 */
@JsonSerialize(using = PrefixQuery.PrefixQuerySerializer::class)
data class PrefixQuery(val field: String, val prefix: Prefix) : TermLevelQuery {

    class PrefixQuerySerializer : JsonSerializer<PrefixQuery>() {
        override fun serialize(value: PrefixQuery?, gen: JsonGenerator?, serializers: SerializerProvider?) {
            gen?.run {
                value?.let { prefixQuery ->
                    writeStartObject()
                    writeFieldName("prefix")
                    writeStartObject()
                    writeObjectField(prefixQuery.field, prefixQuery.prefix)
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

        fun build() = PrefixQuery(
            field = field ?: error("Field name can't be null"),
            prefix = Prefix(
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
        fun of(field: String, value: String, boost: Double? = null) = PrefixQuery(field, Prefix(value, boost))
    }
}