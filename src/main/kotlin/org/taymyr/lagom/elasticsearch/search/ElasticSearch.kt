package org.taymyr.lagom.elasticsearch.search

import com.lightbend.lagom.javadsl.api.Descriptor
import com.lightbend.lagom.javadsl.api.Service
import com.lightbend.lagom.javadsl.api.Service.named

/**
 * Lagom service wrapper for [Elasticsearch Search APIs](https://www.elastic.co/guide/en/elasticsearch/reference/current/search.html)
 * @author Sergey Morgunov
 */
interface ElasticSearch : Service {

    // TODO

    @JvmDefault
    override fun descriptor(): Descriptor {
        return named("elastic-search")
    }
}