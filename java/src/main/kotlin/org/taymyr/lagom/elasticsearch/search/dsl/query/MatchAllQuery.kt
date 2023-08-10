package org.taymyr.lagom.elasticsearch.search.dsl.query

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-all-query.html).
 */
@JsonSerialize(using = MatchAllQuery.Serializer::class)
data class MatchAllQuery @JvmOverloads constructor(val boost: Double? = null) : Query {

    class Serializer : JsonSerializer<MatchAllQuery>() {
        override fun serialize(value: MatchAllQuery?, gen: JsonGenerator?, serializers: SerializerProvider?) {
            gen?.run {
                value?.let { query ->
                    writeStartObject()
                    writeFieldName("match_all")
                    writeStartObject()
                    query.boost?.let { writeNumberField("boost", it) }
                    writeEndObject()
                    writeEndObject()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        @JvmOverloads
        fun of(boost: Double? = null) = MatchAllQuery(boost)
    }
}
