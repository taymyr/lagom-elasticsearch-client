package org.taymyr.lagom.elasticsearch.search.dsl.query.script

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.taymyr.lagom.elasticsearch.script.Script

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-body.html#request-body-search-script-fields).
 */
@JsonSerialize(using = ScriptField.Serializer::class)
data class ScriptField(val name: String, val script: Script) {

    class Serializer : JsonSerializer<ScriptField>() {
        override fun serialize(value: ScriptField?, gen: JsonGenerator?, serializers: SerializerProvider?) {
            gen?.run {
                value?.let { field ->
                    writeStartObject()
                    writeFieldName(field.name)
                    writeStartObject()
                    writeObjectField("script", field.script)
                    writeEndObject()
                    writeEndObject()
                }
            }
        }
    }
}
