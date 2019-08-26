package org.taymyr.lagom.elasticsearch.search.dsl.query.script

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.taymyr.lagom.elasticsearch.script.Script
import org.taymyr.lagom.elasticsearch.search.dsl.query.Query

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-scripting-using.html#_request_examples).
 */
@JsonSerialize(using = ScriptQuery.Serializer::class)
data class ScriptQuery(val script: Script) : Query {

    class Serializer : JsonSerializer<ScriptQuery>() {
        override fun serialize(value: ScriptQuery?, gen: JsonGenerator?, serializers: SerializerProvider?) {
            gen?.run {
                value?.let { field ->
                    writeStartObject()
                    writeFieldName("script")
                    writeStartObject()
                    writeObjectField("script", field.script)
                    writeEndObject()
                    writeEndObject()
                }
            }
        }
    }
}
