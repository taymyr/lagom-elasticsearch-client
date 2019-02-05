package org.taymyr.lagom.elasticsearch

import com.lightbend.lagom.javadsl.api.Service
import org.taymyr.lagom.elasticsearch.deser.ElasticSerializerFactory
import kotlin.reflect.KFunction

inline fun <T : Function<*>> forceKF(fn: T) = fn as KFunction<*>

/**
 * Abstract service for ElasticSearch.
 */
interface ElasticService : Service {
    @JvmDefault fun objectMapper() = ElasticSerializerFactory.MAPPER
}