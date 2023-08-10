package org.taymyr.lagom.elasticsearch.search.dsl.query

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-all-query.html).
 */
@JsonSerialize(using = MatchNoneQuery.Serializer::class)
class MatchNoneQuery : Query {

    class Serializer : JsonSerializer<MatchNoneQuery>() {
        override fun serialize(value: MatchNoneQuery?, gen: JsonGenerator?, serializers: SerializerProvider?) {
            gen?.run {
                value?.let {
                    writeStartObject()
                    writeFieldName("match_none")
                    writeStartObject()
                    writeEndObject()
                    writeEndObject()
                }
            }
        }
    }
}
