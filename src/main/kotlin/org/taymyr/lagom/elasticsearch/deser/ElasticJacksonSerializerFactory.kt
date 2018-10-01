package org.taymyr.lagom.elasticsearch.deser

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy.SNAKE_CASE
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.lightbend.lagom.javadsl.jackson.JacksonSerializerFactory

/**
 * Serializers factory, adopted to ElasticSearch APIs.
 * @author Sergey Morgunov
 */
object ElasticJacksonSerializerFactory : JacksonSerializerFactory(
    ObjectMapper()
        .registerModule(KotlinModule())
        .setPropertyNamingStrategy(SNAKE_CASE)
        .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setSerializationInclusion(NON_NULL)
)