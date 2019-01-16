package org.taymyr.lagom.elasticsearch.search.dsl.query.term

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-range-query.html)
 */
@JsonSerialize(using = RangeQuery.Serializer::class)
data class RangeQuery(val field: String, val range: Range) : TermLevelQuery {

    internal class Serializer : JsonSerializer<RangeQuery>() {
        override fun serialize(value: RangeQuery?, gen: JsonGenerator?, serializers: SerializerProvider?) {
            gen?.run {
                writeStartObject()
                writeFieldName("range")
                writeStartObject()
                writeObjectField(value?.field, value?.range)
                writeEndObject()
                writeEndObject()
            }
        }
    }

    companion object {
        @JvmStatic fun of(field: String, range: Range) = RangeQuery(field, range)
    }
}