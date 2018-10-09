package org.taymyr.lagom.elasticsearch.deser

import akka.util.ByteString
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy.SNAKE_CASE
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.lightbend.lagom.javadsl.api.deser.StrictMessageSerializer
import com.lightbend.lagom.javadsl.jackson.JacksonSerializerFactory
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkRequest
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkRequestSerializer
import java.lang.reflect.Type

/**
 * Serializers factory, adopted to ElasticSearch APIs.
 */
object ElasticSerializerFactory : JacksonSerializerFactory(
    ObjectMapper()
        .registerModule(KotlinModule())
        .setPropertyNamingStrategy(SNAKE_CASE)
        .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setSerializationInclusion(NON_NULL)
) {
    override fun <MessageEntity : Any?> messageSerializerFor(type: Type?): StrictMessageSerializer<MessageEntity> {
        return when (type) {
            BulkRequest::class.javaObjectType -> BulkRequestSerializer()
            ByteString::class.javaObjectType -> ByteStringMessageSerializer()
            else -> super.messageSerializerFor(type)
        } as StrictMessageSerializer<MessageEntity>
    }
}