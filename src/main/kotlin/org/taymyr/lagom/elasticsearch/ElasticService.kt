package org.taymyr.lagom.elasticsearch

import com.lightbend.lagom.javadsl.api.Service
import org.taymyr.lagom.elasticsearch.deser.ElasticSerializerFactory

/**
 * Abstract service for ElasticSearch.
 */
interface ElasticService : Service {
    @JvmDefault fun objectMapper() = ElasticSerializerFactory.MAPPER
}