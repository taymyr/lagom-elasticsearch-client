package org.taymyr.lagom.elasticsearch.search.dsl.query.term

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-regexp-query.html)
 */
@JsonSerialize(using = RegexpQuery.RegexpQuerySerializer::class)
data class RegexpQuery(val field: String, val regexp: Regexp) : TermLevelQuery {

    class RegexpQuerySerializer : JsonSerializer<RegexpQuery>() {
        override fun serialize(value: RegexpQuery?, gen: JsonGenerator?, serializers: SerializerProvider?) {
            gen?.run {
                value?.let { prefixQuery ->
                    writeStartObject()
                    writeFieldName("regexp")
                    writeStartObject()
                    writeObjectField(prefixQuery.field, prefixQuery.regexp)
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
        private val flags: MutableList<RegexpFlag> = mutableListOf()
        private var maxDeterminizedStates: Int? = null

        fun field(field: String) = apply { this.field = field }
        fun value(value: String) = apply { this.value = value }
        fun boost(boost: Double) = apply { this.boost = boost }
        fun flags(vararg flags: RegexpFlag) = apply { this.flags.addAll(flags) }
        fun maxDeterminizedStates(maxDeterminizedStates: Int) = apply { this.maxDeterminizedStates = maxDeterminizedStates }

        fun build() = RegexpQuery(
            field = field ?: error("Field name can't be null"),
            regexp = Regexp(
                value = value ?: error("Value can't be null"),
                boost = boost,
                flags = if (flags.isNotEmpty()) flags.joinToString("|") else null,
                maxDeterminizedStates = maxDeterminizedStates
            )
        )
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()

        @JvmStatic
        @JvmOverloads
        fun of(field: String, value: String, boost: Double? = null, flags: String? = null, maxDeterminizedStates: Int? = null) =
            RegexpQuery(field, Regexp(value, boost, flags, maxDeterminizedStates))
    }
}