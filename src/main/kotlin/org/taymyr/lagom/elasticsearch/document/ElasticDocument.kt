package org.taymyr.lagom.elasticsearch.document

import com.lightbend.lagom.javadsl.api.Descriptor
import com.lightbend.lagom.javadsl.api.Service
import com.lightbend.lagom.javadsl.api.Service.named

/**
 * Lagom service wrapper for [Elasticsearch Document APIs](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs.html)
 * @author Sergey Morgunov
 */
interface ElasticDocument : Service {

    // TODO

    @JvmDefault
    override fun descriptor(): Descriptor {
        return named("elastic-document")
    }
}