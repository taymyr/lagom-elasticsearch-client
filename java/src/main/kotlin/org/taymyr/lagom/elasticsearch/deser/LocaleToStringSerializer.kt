package org.taymyr.lagom.elasticsearch.deser

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.util.Locale

class LocaleToStringSerializer : JsonSerializer<Locale?>() {

    override fun serialize(value: Locale?, gen: JsonGenerator, serializers: SerializerProvider) {
        if (value == null) {
            gen.writeNull()
        } else {
            gen.writeString("${value.language}-${value.country}")
        }
    }
}
