package org.taymyr.lagom.elasticsearch.deser

import akka.util.ByteString
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.lightbend.lagom.javadsl.api.deser.StrictMessageSerializer
import com.lightbend.lagom.javadsl.jackson.JacksonSerializerFactory
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkRequest
import org.taymyr.lagom.elasticsearch.search.dsl.SearchResult
import java.lang.reflect.Type

/**
 * Serializers factory, adopted to ElasticSearch APIs.
 */
class ElasticSerializerFactory(val mapper: ObjectMapper = MAPPER) : JacksonSerializerFactory(mapper) {
    @Suppress("UNCHECKED_CAST")
    override fun <MessageEntity : Any?> messageSerializerFor(type: Type?): StrictMessageSerializer<MessageEntity> {
        return when (type) {
            BulkRequest::class.javaObjectType -> BulkRequestSerializer(mapper)
            ByteString::class.javaObjectType -> ByteStringMessageSerializer()
            is Class<*> ->
                if (SearchResult::class.java.isAssignableFrom(type)) SearchResultSerializer(mapper, type)
                else super.messageSerializerFor(type)
            else -> super.messageSerializerFor(type)
        } as StrictMessageSerializer<MessageEntity>
    }

    companion object {
        @JvmStatic val MAPPER: ObjectMapper = ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(JavaTimeModule())
            .registerModule(ParameterNamesModule())
            .disable(WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .disable(FAIL_ON_UNKNOWN_PROPERTIES)
            .setSerializationInclusion(NON_NULL)
    }
}